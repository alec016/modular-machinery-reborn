package es.degrassi.mmr.api.uistate;

import es.degrassi.mmr.api.save.UIStateSavedData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UIStates {
  @Nullable
  private static volatile IUIStateAccess serverAccess;
  @Nullable
  private static volatile IUIStateAccess clientAccess;

  public static void registerServerAccess(IUIStateAccess access) {
    serverAccess = access;
  }

  public static void registerClientAccess(IUIStateAccess access) {
    clientAccess = access;
  }

  public static <S extends UIState> S get(Player player, BlockPos pos, IBlockUIState<S> state) {
    var access = player instanceof ServerPlayer ? serverAccess : clientAccess;
    return access == null ? state.fallback() : access.get(player, pos, state);
  }

  public static boolean has(Player player, BlockPos pos, IBlockUIState<?> state) {
    var access = player instanceof ServerPlayer ? serverAccess : clientAccess;
    return access != null && access.has(player, pos, state);
  }

  public static <S extends UIState> void put(Player player, BlockPos pos, IBlockUIState<S> state, @Nullable S value) {
    var access = player instanceof ServerPlayer ? serverAccess : clientAccess;
    if (access != null) {
      access.put(player, pos, state, value);
    }
  }

  /**
   * Drops the stored state of a position for every player: the block it belongs to is gone, so
   * keeping it would restore a stale state when another block is placed at the same position.
   */
  public static void remove(BlockPos pos) {
    if (serverAccess != null) {
      serverAccess.remove(pos);
    }
    if (clientAccess != null) {
      clientAccess.remove(pos);
    }
  }

  public static Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> exportAll(Player player) {
    return Optional.ofNullable(serverAccess).map(a -> a.exportAll(player)).orElseGet(HashMap::new);
  }
}
