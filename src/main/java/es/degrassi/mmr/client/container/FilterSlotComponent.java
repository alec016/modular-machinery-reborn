package es.degrassi.mmr.client.container;

/**
 * LEGACY UI - replaced by the LDLib2 {@code ModularUI} screens in {@code es.degrassi.mmr.client.screen.*UI}.
 *
 * This type is kept as an empty shell so that legacy references still resolve; the original
 * implementation is commented out below for reference. Nothing here is registered any more
 * (menus are opened through {@code BlockUIMenuType.openUI}).
 */
// ==== original implementation, commented out ====

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

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class FilterSlotComponent<T, E> extends SlotItemComponent {
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

   //Call from the client only, will ask the server to place the item in the slot to avoid desync issues.
   public void setFromClient(ItemStack stack) {
     ItemStack copy = stack.copyWithCount(1);
     ClientPacketDistributor.sendToServer(new CSetFilterSlotItemPacket(copy, entity.getBlockPos()));
   }

   @Override
   public boolean mayPlace(ItemStack stack) {
     ItemStack copy = stack.copyWithCount(1);
     ClientPacketDistributor.sendToServer(new CSetFilterSlotItemPacket(copy, entity.getBlockPos()));
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
     this.getComponent().setStack(getSlotIndex(), ItemStack.EMPTY);
     return Optional.empty();
   }
 }

