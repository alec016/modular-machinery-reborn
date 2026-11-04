package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.ClientUIStates;
import es.degrassi.mmr.api.save.UIStateSavedData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SSyncUIStatesPacket(Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> uiStates) implements CustomPacketPayload {
  public static final Type<SSyncUIStatesPacket> TYPE = new Type<>(ModularMachineryReborn.rl("sync_ui_states"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncUIStatesPacket> CODEC = UIStateSavedData.STREAM_CODEC.map(
      SSyncUIStatesPacket::new,
      SSyncUIStatesPacket::uiStates
  );

  @Override
  public Type<SSyncUIStatesPacket> type() {
    return TYPE;
  }

  public static void handle(SSyncUIStatesPacket packet, IPayloadContext context) {
    ClientUIStates.register();
    ClientUIStates.getInstance().replaceAll(packet.uiStates);
  }
}
