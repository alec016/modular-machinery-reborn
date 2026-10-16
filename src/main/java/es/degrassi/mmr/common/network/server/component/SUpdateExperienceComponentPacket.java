package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.base.ExperienceHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateExperienceComponentPacket(long amount, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateExperienceComponentPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_experience"));

  @Override
  public Type<SUpdateExperienceComponentPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateExperienceComponentPacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_LONG,
      SUpdateExperienceComponentPacket::amount,
      BlockPos.STREAM_CODEC,
      SUpdateExperienceComponentPacket::pos,
      SUpdateExperienceComponentPacket::new
  );

  public static void handle(SUpdateExperienceComponentPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof ExperienceHatchEntity entity) {
          entity.getTank().setExperience(0, packet.amount);
        }
      });
  }
}
