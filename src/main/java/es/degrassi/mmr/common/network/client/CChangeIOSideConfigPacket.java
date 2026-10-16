package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.config.IOSideConfig;
import es.degrassi.mmr.api.capability.config.ISideConfigComponent;
import es.degrassi.mmr.api.capability.config.RelativeSide;
import es.degrassi.mmr.client.container.ContainerBase;
import es.degrassi.mmr.api.entity.IAutoEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CChangeIOSideConfigPacket(int containerId, RelativeSide side, boolean next) implements CustomPacketPayload {
  public static final Type<CChangeIOSideConfigPacket> TYPE = new Type<>(ModularMachineryReborn.rl("change_auto_output"));

  public static final StreamCodec<ByteBuf, CChangeIOSideConfigPacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      CChangeIOSideConfigPacket::containerId,
      ByteBufCodecs.fromCodec(RelativeSide.CODEC.codec()),
      CChangeIOSideConfigPacket::side,
      ByteBufCodecs.BOOL,
      CChangeIOSideConfigPacket::next,
      CChangeIOSideConfigPacket::new
  );

  public static void handle(CChangeIOSideConfigPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      context.enqueueWork(() -> {
        if(player.containerMenu.containerId == packet.containerId && player.containerMenu instanceof ContainerBase<?> container) {
          var tile = container.getEntity();
          if (tile instanceof IAutoEntity<?> && tile instanceof ISideConfigComponent<?> sideEntity) {
            var config = sideEntity.getConfig();
            if (config  instanceof IOSideConfig c) {
              if (packet.next)
                c.setNext(packet.side);
              else
                c.setPrevious(packet.side);
            }
          }
        }
      });
    }
  }

  @Override
  public Type<CChangeIOSideConfigPacket> type() {
    return TYPE;
  }
}
