package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SMachineUpdatePacket(Identifier machine, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SMachineUpdatePacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_machine"));

  @Override
  public Type<SMachineUpdatePacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SMachineUpdatePacket> CODEC = StreamCodec.composite(
      Identifier.STREAM_CODEC,
      SMachineUpdatePacket::machine,
      BlockPos.STREAM_CODEC,
      SMachineUpdatePacket::pos,
      SMachineUpdatePacket::new
  );

  public static void handle(SMachineUpdatePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          handleRefreshCustomMachineTilePacket(packet.pos, packet.machine);
        }
      });
  }

  public static void handleRefreshCustomMachineTilePacket(BlockPos pos, Identifier machine) {
    if (Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if (tile instanceof MachineControllerEntity machineTile) {
        machineTile.setMachine(machine);
        machineTile.refreshClientData();
        Minecraft.getInstance().level.sendBlockUpdated(pos, machineTile.getBlockState(), machineTile.getBlockState(), Block.UPDATE_ALL);
      }
    }
  }
}
