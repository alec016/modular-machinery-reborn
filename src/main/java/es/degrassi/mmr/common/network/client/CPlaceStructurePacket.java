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

public record CPlaceStructurePacket(Identifier machine, BlockPos controllerPos, boolean withModifiers) implements CustomPacketPayload {
  public static final Type<CPlaceStructurePacket> TYPE = new Type<>(ModularMachineryReborn.rl("place_structure"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CPlaceStructurePacket> CODEC = new StreamCodec<>() {
    @Override
    public CPlaceStructurePacket decode(RegistryFriendlyByteBuf buf) {
      return new CPlaceStructurePacket(buf.readIdentifier(), buf.readBlockPos(), buf.readBoolean());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, CPlaceStructurePacket value) {
      buffer.writeIdentifier(value.machine);
      buffer.writeBlockPos(value.controllerPos);
      buffer.writeBoolean(value.withModifiers);
    }
  };

  @Override
  public Type<CPlaceStructurePacket> type() {
    return TYPE;
  }

  public static void handle(CPlaceStructurePacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      context.enqueueWork(() -> {
        DynamicMachine machine = ModularMachineryReborn.MACHINES.get(packet.machine);
        if (machine == null || machine == DynamicMachine.DUMMY) return;
        Structure.place(machine, packet.controllerPos, player.level(), player.isCreative(), player, packet.withModifiers);
      });
    }
  }
}
