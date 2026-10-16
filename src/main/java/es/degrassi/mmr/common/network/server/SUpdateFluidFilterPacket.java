package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.FiltereableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public record SUpdateFluidFilterPacket(BlockPos pos, FluidResource value) implements CustomPacketPayload {

  public static final Type<SUpdateFluidFilterPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_fluid_filter"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateFluidFilterPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      SUpdateFluidFilterPacket::pos,
      FluidResource.STREAM_CODEC,
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
        if(context.player().level().getBlockEntity(packet.pos) instanceof FiltereableEntity<?, ?> machine && machine.getFilter() instanceof FluidResource) {
          ((FiltereableEntity<FluidResource, Fluid>)machine).setFilter(packet.value);
        }
      });
  }
}
