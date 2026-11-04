package es.degrassi.mmr.client.container;

import es.degrassi.mmr.api.entity.FiltereableEntity;
import es.degrassi.mmr.api.integration.jei.RegisterJeiFilterDragDropEvent;
import es.degrassi.mmr.client.integration.jei.MMRJeiClientIntegration;
import es.degrassi.mmr.common.network.client.CSetFilterSlotItemPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class FilterSlotComponent<T, E> extends MMRBusSlot {
  @Getter
  private final FiltereableEntity<T, E> entity;

  public FilterSlotComponent(FiltereableEntity<T, E> entity, int slot, int x, int y) {
    super(entity.getFilterInventory(), slot, x, y);
    this.entity = entity;
  }

  /**
   * @see MMRJeiClientIntegration#registerFilterDragDrop(RegisterJeiFilterDragDropEvent) for JEI implementation example
   * @param constructor Packet that will be sent once applied
   */
  public void setGenericFromClient(Function<BlockPos, CustomPacketPayload> constructor) {
    ClientPacketDistributor.sendToServer(constructor.apply(entity.getBlockPos()));
  }

  public void setFromClient(ItemStack stack) {
    ItemStack copy = stack.copyWithCount(1);
    ClientPacketDistributor.sendToServer(new CSetFilterSlotItemPacket(copy, entity.getBlockPos()));
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return false;
  }

  @Override
  public boolean mayPickup(Player player) {
    return false;
  }

  @Override
  public boolean isFake() {
    return true;
  }

  @Nullable
  public Component getFilter() {
    return entity.getFilterComponent();
  }

  public E toFilterRender() {
    return entity.toFilterRender();
  }

  @Override
  public ItemStack remove(int amount) {
    this.handler.set(getSlotIndex(), ItemResource.EMPTY, 0);
    this.setChanged();
    return ItemStack.EMPTY;
  }

  @Override
  public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
    remove(count);
    return Optional.empty();
  }
}
