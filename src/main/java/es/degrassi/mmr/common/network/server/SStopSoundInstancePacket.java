package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.machine.SoundManagerEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SStopSoundInstancePacket(BlockPos pos) implements CustomPacketPayload {

  public static final Type<SStopSoundInstancePacket> TYPE = new Type<>(ModularMachineryReborn.rl("stop_sound_instance"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SStopSoundInstancePacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      SStopSoundInstancePacket::pos,
      SStopSoundInstancePacket::new
  );

  @Override
  public Type<SStopSoundInstancePacket> type() {
    return TYPE;
  }

  public static void handle(SStopSoundInstancePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound() && context.player().level().getBlockEntity(packet.pos) instanceof SoundManagerEntity entity) {
      if (entity.getSoundManager() != null) {
        entity.getSoundManager().stop();
      }
    }
  }
}
