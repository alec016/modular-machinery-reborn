package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SSyncPauseStatePacket(boolean isPaused, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SSyncPauseStatePacket> TYPE = new Type<>(ModularMachineryReborn.rl("pause_status"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncPauseStatePacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL,
      SSyncPauseStatePacket::isPaused,
      BlockPos.STREAM_CODEC,
      SSyncPauseStatePacket::pos,
      SSyncPauseStatePacket::new
  );

  @Override
  public Type<SSyncPauseStatePacket> type() {
    return TYPE;
  }

  public static void handle(SSyncPauseStatePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound() && context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
      entity.setPaused(packet.isPaused());
    }
  }
}
