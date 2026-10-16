package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateCraftingStatusPacket(MachineStatus status, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateCraftingStatusPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_crafting_status"));

  @Override
  public Type<SUpdateCraftingStatusPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateCraftingStatusPacket> CODEC = StreamCodec.composite(
      NeoForgeStreamCodecs.enumCodec(MachineStatus.class),
      SUpdateCraftingStatusPacket::status,
      BlockPos.STREAM_CODEC,
      SUpdateCraftingStatusPacket::pos,
      SUpdateCraftingStatusPacket::new
  );

  public static void handle(SUpdateCraftingStatusPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (Minecraft.getInstance().level != null) {
          BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(packet.pos);
          if (tile instanceof MachineControllerEntity machineTile && packet.status != machineTile.getStatus()) {
            machineTile.setStatus(packet.status);
            machineTile.refreshClientData();
            Minecraft.getInstance().level.sendBlockUpdated(packet.pos, tile.getBlockState(), tile.getBlockState(), Block.UPDATE_ALL);
          }
        }
      });
  }
}
