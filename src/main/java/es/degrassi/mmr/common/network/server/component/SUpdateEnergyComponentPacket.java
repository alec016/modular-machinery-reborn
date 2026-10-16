package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateEnergyComponentPacket(long amount, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateEnergyComponentPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_energy"));
  @Override
  public Type<SUpdateEnergyComponentPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateEnergyComponentPacket> CODEC = StreamCodec.composite(
    ByteBufCodecs.VAR_LONG,
    SUpdateEnergyComponentPacket::amount,
    BlockPos.STREAM_CODEC,
    SUpdateEnergyComponentPacket::pos,
    SUpdateEnergyComponentPacket::new
  );

  public static void handle(SUpdateEnergyComponentPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof EnergyHatchEntity entity) {
          entity.setCurrentEnergy(packet.amount);
        }
      });
  }
}
