package es.degrassi.mmr.common.util;

import es.degrassi.mmr.common.network.client.CGridChunkPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Registry of the in-progress scrolling-grid compiles, keyed by the compiling player.
 *
 * <p>The scrolling panes compile progressively (client-driven): the client creates each chunk of
 * slot widgets and then acknowledges it through {@link CGridChunkPacket}, and only then the server
 * creates its own matching slots. Compiling the server strictly after the client's acknowledgement
 * keeps the two container menus ordered, which vanilla's unbounded slot indexing requires. The
 * client compiles a chunk <i>before</i> acknowledging it, so the server's sync packets for that
 * chunk can never reference a slot the client lacks.</p>
 */
public final class GridChunkRegistry {
  private GridChunkRegistry() {}

  private static final Map<UUID, Sink> SINKS = new HashMap<>();

  public interface Sink {
    BlockPos pos();

    /** Compiles {@code chunk} if it is this sink's expected next chunk. Returns true on success. */
    boolean acceptChunk(int chunk);
  }

  public static void register(UUID player, Sink sink) {
    SINKS.put(player, sink);
  }

  public static void unregister(UUID player) {
    SINKS.remove(player);
  }

  public static void handleServerChunk(ServerPlayer player, BlockPos pos, int chunk) {
    var sink = SINKS.get(player.getUUID());
    if (sink == null || !sink.pos().equals(pos)) {
      return;
    }
    // The sink must still belong to the player's currently open menu, otherwise the acknowledgement
    // is stale (the UI was closed or reopened mid-compile).
    AbstractContainerMenu menu = player.containerMenu;
    if (menu == player.inventoryMenu) {
      return;
    }
    sink.acceptChunk(chunk);
  }
}
