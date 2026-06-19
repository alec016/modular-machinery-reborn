package es.degrassi.mmreborn.common.network.client;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.handler.FilterConverterRegistry;
import es.degrassi.mmreborn.common.entity.base.FiltereableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
              .filter(entry ->
                  entry.getValue().get().getClass().equals(fluid.getClass())
                      && FluidStack.isSameFluidSameComponents(((FluidStack) entry.getValue().get()), fluid)
              )
              .findFirst()
              .map(Map.Entry::getKey)
              .ifPresent(item -> machine.getFilterInventory().setItem(0, item.getDefaultInstance()));
        }
      });
  }
}
