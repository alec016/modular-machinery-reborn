package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateFuelComponentPacket(long fuel, long maxFuel, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateFuelComponentPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_fuel"));

  @Override
  public Type<SUpdateFuelComponentPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateFuelComponentPacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_LONG,
      SUpdateFuelComponentPacket::fuel,
      ByteBufCodecs.VAR_LONG,
      SUpdateFuelComponentPacket::maxFuel,
      BlockPos.STREAM_CODEC,
      SUpdateFuelComponentPacket::pos,
      SUpdateFuelComponentPacket::new
  );

  public static void handle(SUpdateFuelComponentPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof FuelTankEntity entity) {
          entity.getFuelHandler().setFuel(packet.fuel);
          entity.getFuelHandler().setMaxFuel(packet.maxFuel);
        }
      });
  }
}
