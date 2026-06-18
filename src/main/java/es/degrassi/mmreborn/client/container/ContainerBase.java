package es.degrassi.mmreborn.client.container;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.api.network.IData;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.api.network.syncable.IntegerSyncable;
import es.degrassi.mmreborn.api.network.syncable.ItemStackSyncable;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.network.server.SUpdateContainerPacket;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public abstract class ContainerBase<T extends ColorableMachineComponentEntity> extends AbstractContainerMenu {
  protected final Player player;
  protected final T entity;
  protected final List<ISyncable<?, ?>> stuffToSync = Lists.newArrayList();
  protected final List<SlotItemComponent> inputSlotComponents = new ArrayList<>();
  protected int firstComponentSlotIndex = 0;

  protected <C extends ContainerBase<T>>ContainerBase(T entity, Player player, @Nullable MenuType<C> menuType, int containerId) {
    super(menuType, containerId);
    this.player = player;
    this.entity = entity;
    init();
  }

  public void init() {
    this.stuffToSync.clear();
    this.stuffToSync.add(ItemStackSyncable.create(this::getCarried, this::setCarried));
    if (entity instanceof ISyncableStuff syncableStuff) {
      syncableStuff.getStuffToSync(this.stuffToSync::add);
    }
    this.slots.clear();
    this.inputSlotComponents.clear();

    AtomicInteger slotIndex = new AtomicInteger(0);
    addPlayerSlots(slotIndex);
    this.firstComponentSlotIndex = slotIndex.get() + 1;
  }

  /**
   * Adds an item slot to this container
   */
  protected Slot addSlot(Slot slot) {
    slot = super.addSlot(slot);
    if (slot instanceof SlotItemComponent sic && !(sic instanceof FilterSlotComponent<?, ?>)) {
      this.inputSlotComponents.add(sic);
    }
    return slot;
  }

  public boolean needFullSync() {
    return needFullSync(100);
  }

  public boolean needFullSync(int ticks) {
    return this.entity.getLevel() != null && this.entity.getLevel().getGameTime() % ticks == 0;
  }

  @Override
  public void clicked(int slotId, int button, ClickType clickType, Player player) {
    getEntity().setChanged();
    super.clicked(slotId, button, clickType, player);
  }

  @Override
  public void broadcastChanges() {
    if (this.player != null && player instanceof ServerPlayer sp) {
      if (this.needFullSync()) {
        List<IData<?>> toSync = new ArrayList<>();
        for (short id = 0; id < this.stuffToSync.size(); id++)
          toSync.add(this.stuffToSync.get(id).getData(id));
        PacketDistributor.sendToPlayer(sp, new SUpdateContainerPacket(this.containerId, toSync));
        return;
      }
      List<IData<?>> toSync = new ArrayList<>();
      for (short id = 0; id < this.stuffToSync.size(); id++) {
        if (this.stuffToSync.get(id).needSync())
          toSync.add(this.stuffToSync.get(id).getData(id));
      }
      if (!toSync.isEmpty())
        PacketDistributor.sendToPlayer(sp, new SUpdateContainerPacket(this.containerId, toSync));
    }
  }

  @Override
  protected DataSlot addDataSlot(DataSlot intReferenceHolder) {
    this.stuffToSync.add(IntegerSyncable.create(intReferenceHolder::get, intReferenceHolder::set));
    return intReferenceHolder;
  }

  @Override
  protected void addDataSlots(ContainerData array) {
    for (int i = 0; i < array.getCount(); i++) {
      int index = i;
      this.stuffToSync.add(IntegerSyncable.create(() -> array.get(index), integer -> array.set(index, integer)));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void handleData(IData<?> data) {
    short id = data.getID();
    ISyncable syncable = this.stuffToSync.get(id);
    if (syncable != null)
      syncable.set(data.getValue());
  }

  protected Slot addSyncedSlot(Slot slot) {
    this.stuffToSync.add(ItemStackSyncable.create(slot::getItem, slot::set));
    return this.addSlot(slot);
  }

  protected void addPlayerSlots(AtomicInteger slotIndex) {
    for (int i = 0; i < 9; i++) {
      addSyncedSlot(new Slot(player.getInventory(), slotIndex.getAndIncrement(), 8 + i * 18, 142));
    }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        addSyncedSlot(new Slot(player.getInventory(), slotIndex.getAndIncrement(), 8 + j * 18, 84 + i * 18));
      }
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = this.slots.get(index);
    if(slot.getItem().isEmpty())
      return ItemStack.EMPTY;

    if (slot.container == this.player.getInventory()) {
      ItemStack stack = slot.getItem().copy();
      List<SlotItemComponent> components;
      components = this.inputSlotComponents;
      for (SlotItemComponent slotComponent : components) {
        stack = slotComponent.getComponent().insertItemBypassLimit(stack, false);
        if(stack.isEmpty())
          break;
      }
      if(stack.isEmpty())
        slot.remove(slot.getItem().getCount());
      else
        slot.remove(slot.getItem().getCount() - stack.getCount());
    } else {
      if (!(slot instanceof SlotItemComponent slotComponent))
        return ItemStack.EMPTY;
      if (slotComponent instanceof FilterSlotComponent<?, ?> fs) {
        fs.remove(Integer.MAX_VALUE);
        fs.setChanged();
        return ItemStack.EMPTY;
      }

      ItemStack removed = slotComponent.getItem();
      if(!moveItemStackTo(removed, 0, this.firstComponentSlotIndex - 1, false))
        return ItemStack.EMPTY;
      slotComponent.setChanged();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.level().getBlockState(this.entity.getBlockPos()) == this.entity.getBlockState() &&
        player.level().getBlockEntity(this.entity.getBlockPos()) == this.entity &&
        player.position().distanceToSqr(Vec3.atCenterOf(this.entity.getBlockPos())) <= 64;
  }
}
