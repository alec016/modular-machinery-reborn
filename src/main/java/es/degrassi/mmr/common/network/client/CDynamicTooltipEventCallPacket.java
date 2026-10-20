package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.client.container.ControllerContainer;
import es.degrassi.mmr.common.integration.kubejs.KubeJSIntegration;
import es.degrassi.mmr.common.network.server.SSyncDynamicTooltipsPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record CDynamicTooltipEventCallPacket(int windowId) implements CustomPacketPayload {
  public static final Type<CDynamicTooltipEventCallPacket> TYPE = new Type<>(ModularMachineryReborn.rl("dynamic_tooltip_event_call"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CDynamicTooltipEventCallPacket> CODEC = StreamCodec.composite(
    ByteBufCodecs.INT,
    CDynamicTooltipEventCallPacket::windowId,
    CDynamicTooltipEventCallPacket::new
  );

  @Override
  public Type<CDynamicTooltipEventCallPacket> type() {
    return TYPE;
  }

  public static void handle(CDynamicTooltipEventCallPacket packet, IPayloadContext context) {
    /*if (context.player() instanceof ServerPlayer player
        && player.containerMenu.containerId == packet.windowId()
        && player.containerMenu instanceof ControllerContainer container
    ) {
      Optional.ofNullable(ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(container.getId()))
              .map(map -> map.get(TooltipUse.GUI))
              .ifPresent(tooltips -> {
                Int2ObjectArrayMap<Component> map = new Int2ObjectArrayMap<>();
                for (int i = 0; i < tooltips.size(); i++) {
                  var either = tooltips.get(i);
                  int finalI = i;
                  either.ifRight(c -> map.put(finalI, c));
                  either.ifLeft(eventId -> map.put(finalI, KubeJSIntegration.sendDynamicTooltipEvent(eventId)));
                }
                PacketDistributor.sendToPlayer(player, new SSyncDynamicTooltipsPacket(container.getId(), map));
              });
    }*/
  }
}
