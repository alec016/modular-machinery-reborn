package es.degrassi.mmr.common.network.server.component;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.CapabilityInventoryEntity;
import es.degrassi.mmr.api.entity.DataComponentInventoryEntity;
import es.degrassi.mmr.common.entity.base.TileInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateItemComponentPacket(int slot, ItemStack item, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateItemComponentPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_item"));
  @Override
  public Type<SUpdateItemComponentPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateItemComponentPacket> CODEC = StreamCodec.composite(
    ByteBufCodecs.INT,
    SUpdateItemComponentPacket::slot,
    ItemStack.OPTIONAL_STREAM_CODEC,
    SUpdateItemComponentPacket::item,
    BlockPos.STREAM_CODEC,
    SUpdateItemComponentPacket::pos,
    SUpdateItemComponentPacket::new
  );

  public static void handle(SUpdateItemComponentPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        var be = context.player().level().getBlockEntity(packet.pos);
        if (be instanceof TileInventory entity) {
          entity.getInventory().setStack(packet.slot, packet.item);
        } else if (be instanceof CapabilityInventoryEntity<?> entity) {
          entity.getCapabilityInventory().setStack(packet.slot, packet.item);
        } else if (be instanceof DataComponentInventoryEntity<?> entity) {
          entity.getDataComponentInventory().setStack(packet.slot, packet.item);
        }
      });
  }
}
