package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.base.FiltereableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateFilterInvPacket(BlockPos pos, ItemStack value) implements CustomPacketPayload {

  public static final Type<SUpdateFilterInvPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_filter_inv"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateFilterInvPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      SUpdateFilterInvPacket::pos,
      ItemStack.OPTIONAL_STREAM_CODEC,
      SUpdateFilterInvPacket::value,
      SUpdateFilterInvPacket::new
  );

  @Override
  public Type<SUpdateFilterInvPacket> type() {
    return TYPE;
  }

  public static void handle(SUpdateFilterInvPacket packet, IPayloadContext context) {
    if(context.flow().isClientbound())
      context.enqueueWork(() -> {
        if(context.player().level().getBlockEntity(packet.pos) instanceof FiltereableEntity<?, ?> machine) {
          machine.getFilterInventory().setItem(0, packet.value);
        }
      });
  }
}
