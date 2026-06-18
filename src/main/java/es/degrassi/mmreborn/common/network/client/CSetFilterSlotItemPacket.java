package es.degrassi.mmreborn.common.network.client;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.handler.FilterConverterRegistry;
import es.degrassi.mmreborn.common.entity.base.FiltereableEntity;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CSetFilterSlotItemPacket(ItemStack stack, BlockPos pos) implements CustomPacketPayload {

  public static final Type<CSetFilterSlotItemPacket> TYPE = new Type<>(ModularMachineryReborn.rl("set_filter_slot"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CSetFilterSlotItemPacket> CODEC = StreamCodec.composite(
      ItemStack.STREAM_CODEC,
      CSetFilterSlotItemPacket::stack,
      BlockPos.STREAM_CODEC,
      CSetFilterSlotItemPacket::pos,
      CSetFilterSlotItemPacket::new
  );

  @Override
  public Type<CSetFilterSlotItemPacket> type() {
    return TYPE;
  }

  public static <T> void handle(CSetFilterSlotItemPacket packet, IPayloadContext context) {
    if(context.player() instanceof ServerPlayer player)
      context.enqueueWork(() -> {
        if(player.level().getBlockEntity(packet.pos) instanceof FiltereableEntity<?, ?> machine) {
          machine.getFilterInventory().setItem(0, packet.stack);
        }
      });
  }
}
