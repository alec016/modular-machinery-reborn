package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.api.integration.emi.RegisterEmiFilterDragDropEvent;
import es.degrassi.mmreborn.api.integration.jei.RegisterJeiFilterDragDropEvent;
import es.degrassi.mmreborn.client.integration.emi.MMREmiClientIntegration;
import es.degrassi.mmreborn.client.integration.jei.MMRJeiClientIntegration;
import es.degrassi.mmreborn.common.entity.base.FiltereableEntity;
import es.degrassi.mmreborn.common.manager.handler.slot.ItemSlot;
import es.degrassi.mmreborn.common.network.client.CSetFilterSlotItemPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class FilterSlotComponent<T, E> extends SlotItemComponent {
  @Getter
  private final FiltereableEntity<T, E> entity;
  public FilterSlotComponent(FiltereableEntity<T, E> entity, ItemSlot container, int slot, int x, int y) {
    super(container, slot, x, y);
    this.entity = entity;
  }

  /**
   * @see MMRJeiClientIntegration#registerFilterDragDrop(RegisterJeiFilterDragDropEvent) for JEI implementation example
   * @see MMREmiClientIntegration#registerEmiFilterDragDrop(RegisterEmiFilterDragDropEvent) for EMI implementation example
   * @param constructor Packet that will be sent once applied
   */
  public void setGenericFromClient(Function<BlockPos, CustomPacketPayload> constructor) {
    PacketDistributor.sendToServer(constructor.apply(entity.getBlockPos()));
  }

  //Call from the client only, will ask the server to place the item in the slot to avoid desync issues.
  public void setFromClient(ItemStack stack) {
    ItemStack copy = stack.copyWithCount(1);
    PacketDistributor.sendToServer(new CSetFilterSlotItemPacket(copy, entity.getBlockPos()));
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    ItemStack copy = stack.copyWithCount(1);
    PacketDistributor.sendToServer(new CSetFilterSlotItemPacket(copy, entity.getBlockPos()));
    return false;
  }

  @Override
  public boolean mayPickup(Player player) {
    return true;
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
  public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
    this.getComponent().setItemStack(ItemStack.EMPTY);
    return Optional.empty();
  }
}
