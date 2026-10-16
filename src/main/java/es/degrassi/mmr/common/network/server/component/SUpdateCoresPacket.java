package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.ParallelHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateCoresPacket(int cores, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateCoresPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_cores"));

  @Override
  public Type<SUpdateCoresPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateCoresPacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      SUpdateCoresPacket::cores,
      BlockPos.STREAM_CODEC,
      SUpdateCoresPacket::pos,
      SUpdateCoresPacket::new
  );

  public static void handle(SUpdateCoresPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      context.enqueueWork(() -> {
        Level level = context.player().level();
        if (level.getBlockEntity(packet.pos) instanceof ParallelHatchEntity entity) {
          entity.setCores(packet.cores);
        }
      });
    }
  }
}
