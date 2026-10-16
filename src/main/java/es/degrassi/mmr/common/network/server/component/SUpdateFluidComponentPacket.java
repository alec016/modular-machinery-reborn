package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.base.FluidTankEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateFluidComponentPacket(int slot, FluidStack fluid, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateFluidComponentPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_fluid"));
  @Override
  public Type<SUpdateFluidComponentPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateFluidComponentPacket> CODEC = StreamCodec.composite(
    ByteBufCodecs.INT,
    SUpdateFluidComponentPacket::slot,
    FluidStack.OPTIONAL_STREAM_CODEC,
    SUpdateFluidComponentPacket::fluid,
    BlockPos.STREAM_CODEC,
    SUpdateFluidComponentPacket::pos,
    SUpdateFluidComponentPacket::new
  );

  public static void handle(SUpdateFluidComponentPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof FluidTankEntity entity) {
          entity.getTank().setFluid(packet.slot, packet.fluid);
        }
      });
  }
}
