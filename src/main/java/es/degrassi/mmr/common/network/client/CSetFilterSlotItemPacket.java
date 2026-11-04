package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.FiltereableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.item.ItemResource;

public record CSetFilterSlotItemPacket(ItemStack stack, BlockPos pos) implements CustomPacketPayload {

  public static final Type<CSetFilterSlotItemPacket> TYPE = new Type<>(ModularMachineryReborn.rl("set_item_filter_slot"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CSetFilterSlotItemPacket> CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC,
      CSetFilterSlotItemPacket::stack,
      BlockPos.STREAM_CODEC,
      CSetFilterSlotItemPacket::pos,
      CSetFilterSlotItemPacket::new
  );

  @Override
  public Type<CSetFilterSlotItemPacket> type() {
    return TYPE;
  }

  public static void handle(CSetFilterSlotItemPacket packet, IPayloadContext context) {
    if(context.player() instanceof ServerPlayer player)
      context.enqueueWork(() -> {
        if(player.level().getBlockEntity(packet.pos) instanceof FiltereableEntity<?, ?> machine) {
          var stack = packet.stack();
          machine.getFilterInventory()
              .set(
                  0,
                  ItemResource.of(stack),
                  stack.isEmpty() ? 0 : Math.min(stack.getCount(), machine.getFilterInventory().getCapacity(0, ItemResource.of(stack)))
              );
        }
      });
  }
}
