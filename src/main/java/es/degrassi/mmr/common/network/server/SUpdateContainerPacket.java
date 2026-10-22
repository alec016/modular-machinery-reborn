package es.degrassi.mmr.common.network.server;

import es.degrassi.experiencelib.api.network.Data;
import es.degrassi.experiencelib.api.network.ISyncableHandler;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SUpdateContainerPacket(int windowId, List<Data<?>> data) implements CustomPacketPayload {
  public static final Type<SUpdateContainerPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_container"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateContainerPacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      SUpdateContainerPacket::windowId,
      Data.STREAM_CODEC.apply(ByteBufCodecs.list()),
      SUpdateContainerPacket::data,
      SUpdateContainerPacket::new
  );

  @Override
  public Type<SUpdateContainerPacket> type() {
    return TYPE;
  }

  public static void handle(SUpdateContainerPacket packet, IPayloadContext context) {
    if(context.flow().isClientbound())
      context.enqueueWork(() -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null && player.containerMenu instanceof ISyncableHandler container && player.containerMenu.containerId == packet.windowId) {
          packet.data.forEach(container::handleData);
        }
      });
  }
}
