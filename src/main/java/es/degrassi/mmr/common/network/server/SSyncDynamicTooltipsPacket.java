package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.container.ControllerContainer;
import es.degrassi.mmr.common.util.MMRLogger;
import es.degrassi.mmr.common.util.TextComponentUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SSyncDynamicTooltipsPacket(Identifier machineId, Int2ObjectArrayMap<Component> tooltips) implements CustomPacketPayload {
  public static final Type<SSyncDynamicTooltipsPacket> TYPE = new Type<>(ModularMachineryReborn.rl("sync_dynamic_tooltips"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncDynamicTooltipsPacket> CODEC = new StreamCodec<>() {
    @Override
    public SSyncDynamicTooltipsPacket decode(RegistryFriendlyByteBuf buf) {
      var rl = buf.readIdentifier();
      var tooltips = buf.readMap(ByteBufCodecs.INT, TextComponentUtils.CODEC::fromNetwork);
      Int2ObjectArrayMap<Component> components = new Int2ObjectArrayMap<>();
      components.putAll(tooltips);
      return new SSyncDynamicTooltipsPacket(rl, components);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SSyncDynamicTooltipsPacket packet) {
      try {
        buf.writeIdentifier(packet.machineId);
        buf.writeMap(packet.tooltips, ByteBufCodecs.INT, TextComponentUtils.CODEC::toNetwork);
      } catch (Exception e) {
        MMRLogger.INSTANCE.error("Encode error: ", e);
      }
    }
  };

  @Override
  public Type<SSyncDynamicTooltipsPacket> type() {
    return TYPE;
  }

  public static void handle(SSyncDynamicTooltipsPacket packet, IPayloadContext context) {
    // LEGACY: the vanilla-menu UI this relied on was replaced by the LDLib2 ModularUI screens
    // (es.degrassi.mmr.client.screen.*UI + BlockUIMenuType). There is no ContainerBase menu to
    // act on any more, so this is a no-op until it is re-implemented on top of LDLib2.
}
}
