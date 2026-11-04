package es.degrassi.mmr.client;

import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.api.uistate.IUIStateAccess;
import es.degrassi.mmr.api.uistate.UIState;
import es.degrassi.mmr.api.uistate.UIStates;
import es.degrassi.mmr.api.save.UIStateSavedData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientUIStates implements IUIStateAccess {
  private static final ClientUIStates INSTANCE = new ClientUIStates();

  private final Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> states = new HashMap<>();

  public static void register() {
    UIStates.registerClientAccess(INSTANCE);
  }

  public static ClientUIStates getInstance() {
    return INSTANCE;
  }

  private static ResourceKey<Level> dimension(Player player) {
    return player.level().dimension();
  }

  private List<UIStateSavedData.Data> get(Player player)  {
    return states.computeIfAbsent(dimension(player), _ -> new HashMap<>()).computeIfAbsent(player.getUUID(), _ -> new ArrayList<>());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends UIState> S get(Player player, BlockPos pos, IBlockUIState<S> state) {
    var positions = get(player);
    if (positions.isEmpty() || positions.stream().noneMatch(d -> d.pos().equals(pos))) {
      return state.fallback();
    }
    var value = positions.stream().filter(d -> d.pos().equals(pos)).map(UIStateSavedData.Data::data).findFirst();
    return (S) value.orElseGet(state::fallback);
  }

  @Override
  public <S extends UIState> boolean has(Player player, BlockPos pos, IBlockUIState<S> state) {
    var positions = get(player);
    return positions != null && positions.stream().anyMatch(d -> d.pos().equals(pos));
  }

  @Override
  public <S extends UIState> void put(Player player, BlockPos pos, IBlockUIState<S> state, @Nullable S value) {
    putValue(dimension(player), player.getUUID(), pos, value);
  }

  public <S extends UIState> void putValue(ResourceKey<Level> dimension, UUID player, BlockPos pos, @Nullable S value) {
    var positions = states.computeIfAbsent(dimension, _ -> new HashMap<>()).computeIfAbsent(player, _ -> new ArrayList<>());
    positions.removeIf(d -> d.pos().equals(pos));
    if (value == null) return;
    states.computeIfAbsent(dimension, _ -> new HashMap<>()).computeIfAbsent(player, _ -> new ArrayList<>()).add(new UIStateSavedData.Data(pos, value));
  }

  /** Drops the state of a position for every player (the block it belonged to is gone). */
  public void remove(ResourceKey<Level> dimension, BlockPos pos) {
    var byPlayer = states.get(dimension);
    if (byPlayer == null) return;
    byPlayer.values().forEach(list -> list.removeIf(d -> d.pos().equals(pos)));
  }

  @Override
  public void remove(BlockPos pos) {
    // The block the state belonged to is gone: drop it for every player of every dimension.
    states.values().forEach(byPlayer -> byPlayer.values().forEach(list -> list.removeIf(d -> d.pos().equals(pos))));
  }

  @Override
  public Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> exportAll(Player player) {
    return new HashMap<>();
  }

  @Override
  public void replaceAll(@Nullable Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> allDimensions) {
    clear();
    if (allDimensions == null) {
      return;
    }
    for (var dimension : allDimensions.keySet()) {
      var byPlayer = allDimensions.getOrDefault(dimension, new HashMap<>());
      byPlayer.forEach((uuid, positions) -> {
        for (var data : positions) {
          putValue(dimension, uuid, data.pos(), data.data());
        }
      });
    }
  }

  public void clear() {
    states.clear();
  }
}
