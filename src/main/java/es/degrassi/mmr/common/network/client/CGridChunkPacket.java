package es.degrassi.mmr.common.network.client;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.util.GridChunkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-driven grid compile acknowledgement: the client has created the widgets of
 * {@code chunk} for the scrolling pane at {@code pos} and the server may now create its own
 * matching slots. Compiling the server side strictly after the client's acknowledgement keeps the
 * two container menus ordered (the server's sync packets can never reference a slot the client
 * hasn't built yet), which vanilla's slot indexing requires.
 */
public record CGridChunkPacket(BlockPos pos, int chunk) implements CustomPacketPayload {
  public static final Type<CGridChunkPacket> TYPE = new Type<>(ModularMachineryReborn.rl("grid_chunk_ready"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CGridChunkPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CGridChunkPacket::pos,
      ByteBufCodecs.VAR_INT,
      CGridChunkPacket::chunk,
      CGridChunkPacket::new
  );

  @Override
  public Type<CGridChunkPacket> type() {
    return TYPE;
  }

  public static void handle(CGridChunkPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      context.enqueueWork(() -> GridChunkRegistry.handleServerChunk(player, packet.pos(), packet.chunk()));
    }
  }
}
