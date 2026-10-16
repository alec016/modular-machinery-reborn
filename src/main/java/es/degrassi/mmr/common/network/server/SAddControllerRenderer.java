package es.degrassi.mmr.common.network.server;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.entity.renderer.ControllerRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SAddControllerRenderer(Identifier machineId, BlockPos controllerPos) implements CustomPacketPayload {

  public static final Type<SAddControllerRenderer> TYPE = new Type<>(ModularMachineryReborn.rl("add_renderer"));

  public static final StreamCodec<ByteBuf, SAddControllerRenderer> CODEC = StreamCodec.composite(
      Identifier.STREAM_CODEC,
      SAddControllerRenderer::machineId,
      BlockPos.STREAM_CODEC,
      SAddControllerRenderer::controllerPos,
      SAddControllerRenderer::new
  );

  @Override
  public Type<SAddControllerRenderer> type() {
    return TYPE;
  }

  public static void handle(SAddControllerRenderer packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      ControllerRenderer.add(ModularMachineryReborn.MACHINES.get(packet.machineId), packet.controllerPos);
    }
  }
}
