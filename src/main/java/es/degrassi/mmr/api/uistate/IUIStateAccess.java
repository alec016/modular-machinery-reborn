package es.degrassi.mmr.api.uistate;

import es.degrassi.mmr.api.save.UIStateSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IUIStateAccess {
  <S extends UIState> S get(Player player, BlockPos pos, IBlockUIState<S> state);

  <S extends UIState> boolean has(Player player, BlockPos pos, IBlockUIState<S> state);

  <S extends UIState> void put(Player player, BlockPos pos, IBlockUIState<S> state, @Nullable S value);

  /** Removes the stored state of a position (the block it belonged to is gone). */
  void remove(BlockPos pos);

  Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> exportAll(Player player);

  void replaceAll(@Nullable Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> allDimensions);
}
