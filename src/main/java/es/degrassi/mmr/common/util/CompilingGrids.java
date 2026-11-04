package es.degrassi.mmr.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks the scrolling grids that are still compiling.
 *
 * <p>While a grid compiles, its slots do not exist yet on the server side, so accepting items in
 * them would move items into a slot the client does not have (and the shift-click scan would find
 * no target). The grid is therefore locked until the compile is done.</p>
 */
public final class CompilingGrids {
  private static final Set<BlockPos> COMPILING = new HashSet<>();

  private CompilingGrids() {}

  public static void start(ServerLevel level, BlockPos pos) {
    COMPILING.add(pos.immutable());
  }

  public static void stop(ServerLevel level, BlockPos pos) {
    COMPILING.remove(pos);
  }

  public static boolean isCompiling(BlockPos pos) {
    return COMPILING.contains(pos);
  }
}
