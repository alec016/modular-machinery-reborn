package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.base.FiltereableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateFluidFilterPacket(BlockPos pos, FluidStack value) implements CustomPacketPayload {

  public static final Type<SUpdateFluidFilterPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_fluid_filter"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateFluidFilterPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      SUpdateFluidFilterPacket::pos,
      FluidStack.OPTIONAL_STREAM_CODEC,
      SUpdateFluidFilterPacket::value,
      SUpdateFluidFilterPacket::new
  );

  @Override
  public Type<SUpdateFluidFilterPacket> type() {
    return TYPE;
  }

  @SuppressWarnings("unchecked")
  public static void handle(SUpdateFluidFilterPacket packet, IPayloadContext context) {
    if(context.flow().isClientbound())
      context.enqueueWork(() -> {
        if(context.player().level().getBlockEntity(packet.pos) instanceof FiltereableEntity<?, ?> machine && machine.getFilter() instanceof FluidStack) {
          ((FiltereableEntity<FluidStack, Fluid>)machine).setFilter(packet.value);
        }
      });
  }
}
