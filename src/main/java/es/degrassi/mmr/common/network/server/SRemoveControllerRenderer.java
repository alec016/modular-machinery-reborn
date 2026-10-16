package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.entity.renderer.ControllerRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SRemoveControllerRenderer(BlockPos controllerPos) implements CustomPacketPayload {

  public static final Type<SRemoveControllerRenderer> TYPE = new Type<>(ModularMachineryReborn.rl("remove_renderer"));

  public static final StreamCodec<ByteBuf, SRemoveControllerRenderer> CODEC =
      BlockPos.STREAM_CODEC.map(SRemoveControllerRenderer::new, SRemoveControllerRenderer::controllerPos);

  @Override
  public Type<SRemoveControllerRenderer> type() {
    return TYPE;
  }

  public static void handle(SRemoveControllerRenderer packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      ControllerRenderer.renderers.remove(packet.controllerPos);
    }
  }
}
