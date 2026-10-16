package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.common.machine.DynamicMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CBreakStructurePacket(Identifier machine, BlockPos controllerPos) implements CustomPacketPayload {
  public static final Type<CBreakStructurePacket> TYPE = new Type<>(ModularMachineryReborn.rl("break_structure"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CBreakStructurePacket> CODEC = new StreamCodec<>() {
    @Override
    public CBreakStructurePacket decode(RegistryFriendlyByteBuf buf) {
      return new CBreakStructurePacket(buf.readIdentifier(), buf.readBlockPos());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CBreakStructurePacket value) {
      buffer.writeIdentifier(value.machine);
      buffer.writeBlockPos(value.controllerPos);
    }
  };

  @Override
  public Type<CBreakStructurePacket> type() {
    return TYPE;
  }

  public static void handle(CBreakStructurePacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      context.enqueueWork(() -> {
        DynamicMachine machine = ModularMachineryReborn.MACHINES.get(packet.machine);
        if (machine == null) return;
        Structure.breakStructure(machine, packet.controllerPos, player.level(), player);
      });
    }
  }
}
