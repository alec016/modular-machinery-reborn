package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.FiltereableEntity;
import es.degrassi.mmr.api.handler.FilterConverterRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Map;

public record CSetFilterSlotFluidPacket(FluidStack stack, BlockPos pos) implements CustomPacketPayload {

  public static final Type<CSetFilterSlotFluidPacket> TYPE = new Type<>(ModularMachineryReborn.rl("set_fluid_filter_slot"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CSetFilterSlotFluidPacket> CODEC = StreamCodec.composite(
      FluidStack.STREAM_CODEC,
      CSetFilterSlotFluidPacket::stack,
      BlockPos.STREAM_CODEC,
      CSetFilterSlotFluidPacket::pos,
      CSetFilterSlotFluidPacket::new
  );

  @Override
  public Type<CSetFilterSlotFluidPacket> type() {
    return TYPE;
  }

  public static void handle(CSetFilterSlotFluidPacket packet, IPayloadContext context) {
    if(context.player() instanceof ServerPlayer player)
      context.enqueueWork(() -> {
        if(player.level().getBlockEntity(packet.pos) instanceof FiltereableEntity<?, ?> machine) {
          var fluid = packet.stack;
          FilterConverterRegistry.getConverters()
              .entrySet()
              .stream()
              .filter(entry -> entry.getValue().get() instanceof FluidResource resource && resource.matches(fluid))
              .findAny()
              .map(Map.Entry::getKey)
              .ifPresent(item -> {
                var bucket = item.get().copyWithCount(1);
                machine.getFilterInventory().set(0, ItemResource.of(bucket), 1);
              });
        }
      });
  }
}
