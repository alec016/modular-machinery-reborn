package es.degrassi.mmreborn.common.manager.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.common.manager.handler.slot.ItemSlot;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ItemHandler extends AbstractHandler<ItemSlot, ItemStack> implements IItemHandlerModifiable, Container {
  private int[] miscSlots = new int[0];

  private ItemHandler(int slotLimit) {
    super(slotLimit);
  }

  public ItemHandler(int[] inSlots, int[] outSlots, int slotLimit) {
    super(inSlots, outSlots, slotLimit);
  }

  public ItemHandler(int[] inSlots, int[] outSlots, int slotLimit, Direction... accessibleFrom) {
    super(inSlots, outSlots, slotLimit, accessibleFrom);
  }

  public ItemHandler(int[] inSlots, int[] outSlots, Predicate<ItemStack> filter, int slotLimit, Direction... accessibleFrom) {
    super(inSlots, outSlots, filter, slotLimit, accessibleFrom);
  }
  
  public ItemHandler setMiscSlots(int... miscSlots) {
    this.miscSlots = miscSlots;
    for (Integer slot : miscSlots) {
      this.getInventory().add(new ItemSlot(slot, this, 64, 64, 0, item -> true));
    }
    return this;
  }

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    getInventory().stream().filter(s -> s.getSlot() == slot)
        .findFirst()
        .ifPresent(s -> {
          s.setItemStack(stack);
        });
    setChanged(slot, stack);
  }

  public void setStackInSlotNoUpdate(int slot, ItemStack stack) {
    getInventory().stream().filter(s -> s.getSlot() == slot)
        .findFirst()
        .ifPresent(s -> {
          s.setItemStackNoUpdate(stack);
        });
  }

  @Override
  public int getSlotLimit(int slot) {
    return getSlotLimit();
  }

  public int getStackLimit(int slot, ItemStack stack) {
    return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return getInventory().stream()
        .filter(s -> s.getSlot() == slot)
        .findFirst()
        .map(s -> s.isItemValid(0, stack))
        .orElse(false);
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return Optional.ofNullable(getInventory().get(slot)).map(ItemSlot::getItemStack).orElse(ItemStack.EMPTY);
  }

  public ItemStack insertItem(ItemStack stack, boolean simulate) {
    ItemStack toInsert = stack.copy();
    for (int i = 0; i < getSlots(); i++) {
      toInsert = insertItem(i, toInsert.copy(), simulate);
      if (toInsert.isEmpty()) return ItemStack.EMPTY;
    }
    return toInsert;
  }

  public ItemStack extractItem(ItemStack stack, boolean simulate) {
    int toExtract = stack.getCount();
    for (int i = 0; i < getSlots(); i++) {
      if (getItem(i).isEmpty() || !ItemStack.isSameItemSameComponents(getItem(i), stack))
        continue;
      toExtract -= extractItem(i, toExtract, simulate).getCount();
      if (toExtract <= 0) return ItemStack.EMPTY;
    }
    return stack.copyWithCount(toExtract);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return getInventory().get(slot).insertItem(0, stack, simulate);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return getInventory().get(slot).extractItem(0, amount, simulate);
  }

  @Override
  public int getContainerSize() {
    return getSlots();
  }

  @Override
  public ItemStack getItem(int slot) {
    return getStackInSlot(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return extractItem(slot, amount, false);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    ItemStack prevStack = getStackInSlot(slot);
    setStackInSlot(slot, ItemStack.EMPTY);
    return prevStack;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    setStackInSlot(slot, stack);
  }

  public void setItemNoUpdate(int slot, ItemStack stack) {
    setStackInSlotNoUpdate(slot, stack);
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public void clearContent() {
    for (int j = 0; j < getContainerSize(); j++)
      removeItemNoUpdate(j);
  }

  public int getDurabilityAmount(ItemStack stack) {
    return this.getInputs()
        .stream()
        .filter(component -> isSameItem(component.getItemStack(), stack) && component.getItemStack().isDamageableItem())
        .mapToInt(component -> component.getItemStack().getMaxDamage() - component.getItemStack().getDamageValue())
        .sum();
  }

  public int getSpaceForDurability(ItemStack stack) {
    return this.getInputs()
        .stream()
        .filter(component -> isSameItem(component.getItemStack(), stack) && component.getItemStack().isDamageableItem())
        .mapToInt(component -> component.getItemStack().getDamageValue())
        .sum();
  }

  public void repairItem(ItemStack stack, int amount) {
    AtomicInteger toRepair = new AtomicInteger(amount);
    this.getInputs()
        .stream()
        .filter(component -> isSameItem(component.getItemStack(), stack) && component.getItemStack().isDamageableItem())
        .forEach(component -> {
          int maxRepair = Math.min(component.getItemStack().getDamageValue(), toRepair.get());
          toRepair.addAndGet(-maxRepair);
          component.getItemStack().setDamageValue(component.getItemStack().getDamageValue() - maxRepair);
          component.setChanged();
        });
  }

  public void removeDurability(ItemStack input, int amount) {
    AtomicInteger toRemove = new AtomicInteger(amount);
    this.getInputs()
        .stream()
        .filter(component -> isSameItem(component.getItemStack(), input) && component.getItemStack().isDamageableItem())
        .forEach(component -> {
          int maxRemove = Math.min(component.getItemStack().getMaxDamage() - component.getItemStack().getDamageValue(), toRemove.get());
          ItemStack stack = component.getItemStack();
          maxRemove = stack.getItem().damageItem(stack, maxRemove, null, s -> {});
          if (maxRemove > 0) {
            maxRemove = EnchantmentHelper.processDurabilityChange((ServerLevel)getLevel(), stack, maxRemove);
            if (maxRemove <= 0) {
              return;
            }
          }
          toRemove.addAndGet(-maxRemove);
          stack.setDamageValue(stack.getDamageValue() + maxRemove);
          if(stack.getDamageValue() >= stack.getMaxDamage())
            stack.shrink(1);
          component.setChanged();
        });
  }

  private static boolean isSameItem(ItemStack toTest, ItemStack ingredient) {
    if(toTest.getItem() != ingredient.getItem())
      return false;
    return ingredient.getComponents().stream()
        .allMatch(component -> component.type() == DataComponents.DAMAGE || (toTest.has(component.type()) && Objects.equals(toTest.get(component.type()), component.value())));
  }

  protected List<ItemSlot> generateInventory(Predicate<ItemStack> filter) {
    List<ItemSlot> inventory = new ArrayList<>();
    for (Integer slot : inSlots) {
      ItemSlot itemSlot = new ItemSlot(slot, this, getSlotLimit(slot), getSlotLimit(slot), 0, filter);
      this.getInputs().add(itemSlot);
      inventory.add(itemSlot);
    }
    for (Integer slot : outSlots) {
      ItemSlot itemSlot = new ItemSlot(slot, this, getSlotLimit(slot), 0, getSlotLimit(slot), filter);
      this.getOutputs().add(itemSlot);
      inventory.add(itemSlot);
    }
    return inventory;
  }

  public void deserialize(CompoundTag tag, HolderLookup.Provider pRegistries) {
    readNBT(tag, pRegistries);
  }

  public int calcRedstoneFromInventory() {
    int i = 0;
    float f = 0.0F;
    for (int j = 0; j < getSlots(); ++j) {
      ItemStack itemstack = getStackInSlot(j);
      if (!itemstack.isEmpty()) {
        f += (float) itemstack.getCount() / (float) Math.min(getSlotLimit(j), itemstack.getMaxStackSize());
        ++i;
      }
    }
    f = f / (float) getSlots();
    return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);

  }

  public static ItemHandler mergeBuild(ItemHandler... inventories) {
    ItemHandler merged = new ItemHandler(64);
    int slotOffset = 0;
    Map<Integer, ItemHandler> slotLimitIndex = Maps.newHashMap();
    List<Integer> inSlots = Lists.newArrayList();
    List<Integer> outSlots = Lists.newArrayList();
    List<Integer> miscSlots = Lists.newArrayList();
    List<Direction> sides = Lists.newArrayList(Direction.values());
    List<ItemSlot> inputs = Lists.newArrayList();
    List<ItemSlot> outputs = Lists.newArrayList();
    int stackLimit = 0;
    for (ItemHandler inventory : inventories) {
      stackLimit += inventory.getSlotLimit();
      for (ItemSlot key : inventory.getInventory()) {
        merged.getInventory().add(key.getSlot() + slotOffset, key);
      }
      int finalSlotOffset = slotOffset;
      Arrays.stream(inventory.inSlots).map(in -> in + finalSlotOffset).forEach(inSlots::add);
      Arrays.stream(inventory.outSlots).map(out -> out + finalSlotOffset).forEach(outSlots::add);
      Arrays.stream(inventory.miscSlots).map(misc -> misc + finalSlotOffset).forEach(miscSlots::add);
      sides = sides.stream().map(side -> {
        if (inventory.accessibleSides.contains(side))
          return side;
        return null;
      }).filter(Objects::nonNull).toList();
      slotOffset += inventory.getInventory().size();
      slotLimitIndex.put(slotOffset, inventory);
      inputs.addAll(inventory.getInputs());
      outputs.addAll(inventory.getOutputs());
    }
    merged.accessibleSides = sides;
    var builder = IntStream.builder();
    inSlots.forEach(builder::add);
    merged.inSlots = builder.build().toArray();
    builder = IntStream.builder();
    outSlots.forEach(builder::add);
    merged.outSlots = builder.build().toArray();
    builder = IntStream.builder();
    miscSlots.forEach(builder::add);
    merged.miscSlots = builder.build().toArray();
    merged.getInputs().addAll(inputs);
    merged.getOutputs().addAll(outputs);
    merged.slotLimit = stackLimit;
    merged.setListener((slot, stack) ->
        slotLimitIndex.forEach((slotLimit, inventory) -> {
          if (slotLimit < slot)
            inventory.getListener().onChange(slot - slotLimit, stack);
        })
    );
    return merged;
  }

  public List<Slot> createInventorySlots(List<Slot> slots, int i) {
    for (int j = 0; j < getSlots(); j++) {
      slots.add(createSlot(j, i));
    }
    return slots;
  }

  public List<Slot> createSlots(Player player) {
    List<Slot> slots = Lists.newArrayList();
    int i;
    for (i = 0; i < player.getInventory().getContainerSize(); i++) {
      slots.add(new Slot(player.getInventory(), i, 0, 0));
    }
    return createInventorySlots(slots, i);
  }

  private Slot createSlot(int i, int increment) {
    return new Slot(this, i + increment, 0, 0);
  }

  public void removeFromInputs(ItemStack stack, int amount) {
    AtomicInteger toRemove = new AtomicInteger(amount);
    this.getInputs()
        .stream()
        .filter(component -> ItemStack.isSameItemSameComponents(component.getItemStack(), stack))
        .forEach(component -> {
          int maxExtract = toRemove.get() - component.extractItemBypassLimit(stack.getCount(), true).getCount();
          toRemove.addAndGet(-maxExtract);
          component.extractItemBypassLimit(maxExtract, false);
          component.setChanged();
        });
  }

  public void addToOutputs(ItemStack stack, int amount) {
    AtomicInteger toAdd = new AtomicInteger(amount);
    this.getOutputs()
        .stream()
        .filter(component -> canPlaceOutput(component, stack))
        .forEach(component -> {
          int maxInsert = toAdd.get() - component.insertItemBypassLimit(stack, true).getCount();
          toAdd.addAndGet(-maxInsert);
          component.insertItemBypassLimit(stack.copyWithCount(maxInsert), false);
          component.setChanged();
        });
  }

  public boolean canPlaceOutput(ItemSlot component, ItemStack stack) {
    //Check component filter and variant
    if (!component.isItemValid(0, stack))
      return false;

    //If the slot is empty, any item can go inside
    if (component.getItemStack().isEmpty())
      return true;

    //If the item present in the slot in not the same item, they won't stack
    if (!ItemStack.isSameItemSameComponents(component.getItemStack(), stack))
      return false;

    //Check if the stack present in the slot can accept more items
    return component.getItemStack().getCount() < Math.min(stack.getMaxStackSize(), component.getCapacity());
  }

  public int getSpaceForItem(ItemStack stack) {
    return this.getOutputs()
        .stream()
        .filter(component -> canPlaceOutput(component, stack))
        .mapToInt(component -> {
          if (component.getItemStack().isEmpty())
            return Math.min(component.getCapacity(), stack.getMaxStackSize());
          else
            return Math.min(component.getCapacity() - component.getItemStack().getCount(), stack.getMaxStackSize() - component.getItemStack().getCount());
        })
        .sum();
  }

  public int getItemAmount(ItemStack stack) {
    return this.getInputs()
        .stream()
        .filter(component -> ItemStack.isSameItemSameComponents(component.getItemStack(), stack))
        .mapToInt(component -> component.getItemStack().getCount())
        .sum();
  }

  @Override
  public CompoundTag writeNBT(HolderLookup.Provider pRegistries) {
    var tag = super.writeNBT(pRegistries);
    tag.putIntArray("miscSlots", this.miscSlots);
    return tag;
  }

  @Override
  public void readNBT(CompoundTag tag, HolderLookup.Provider pRegistries) {
    super.readNBT(tag, pRegistries);
    this.miscSlots = tag.getIntArray("miscSlots");
  }

  @Override
  protected ItemSlot createSlot(HolderLookup.Provider pRegistries, CompoundTag componentNBT) {
    return new ItemSlot(this, getDefaultFilter(), componentNBT, pRegistries);
  }
}
