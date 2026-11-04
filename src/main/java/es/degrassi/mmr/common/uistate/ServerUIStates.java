package es.degrassi.mmr.common.uistate;

import es.degrassi.mmr.api.save.UIStateSavedData;
import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.api.uistate.IUIStateAccess;
import es.degrassi.mmr.api.uistate.UIState;
import es.degrassi.mmr.api.uistate.UIStates;
import es.degrassi.mmr.common.network.client.SUpdateUIStatePacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access =  AccessLevel.PRIVATE)
public final class ServerUIStates implements IUIStateAccess {
  private static final ServerUIStates INSTANCE = new ServerUIStates();

  public static ServerUIStates getInstance() {
    return INSTANCE;
  }

  public static void init() {
    UIStates.registerServerAccess(INSTANCE);
  }

  private static UIStateSavedData store(Player player) {
    return UIStateSavedData.of((ServerLevel) player.level());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends UIState> S get(Player player, BlockPos pos, IBlockUIState<S> state) {
    var section = store(player).data().computeIfAbsent(player.getUUID(), _ -> new ArrayList<>());
    if (section.isEmpty() || section.stream().noneMatch(d -> d.pos().equals(pos))) {
      return state.fallback();
    }
    var value = section.stream().filter(d -> d.pos().equals(pos)).findFirst().map(UIStateSavedData.Data::data);
    return (S) value.orElseGet(state::fallback);
  }

  @Override
  public <S extends UIState> boolean has(Player player, BlockPos pos, IBlockUIState<S> state) {
    var section = store(player).data().computeIfAbsent(player.getUUID(), _ -> new ArrayList<>());
    return !section.isEmpty() && section.stream().anyMatch(d -> d.pos().equals(pos));
  }

  @Override
  public <S extends UIState> void put(Player player, BlockPos pos, IBlockUIState<S> state, @Nullable S value) {
    if (!(player instanceof ServerPlayer serverPlayer)) {
      return;
    }
    var data = store(serverPlayer);
    if (value == null) {
      value = state.fallback();
    }
    data.put(player, pos, value);
    var update = new SUpdateUIStatePacket(serverPlayer.level().dimension(), player.getUUID(), pos, value);
    PacketDistributor.sendToAllPlayers(update);
  }

  @Override
  public void remove(BlockPos pos) {
    var server = ServerLifecycleHooks.getCurrentServer();
    if (server == null) removeIn(null, null, pos);
    else {
      for (var level : server.getAllLevels())
        removeIn(level, null, pos);
    }
  }

  /**
   * Drops the state of a position from the saved data of the given level (the block it belonged
   * to is gone) and tells the clients, so no stale state is restored at that position later.
   */
  public void removeIn(@Nullable ServerLevel level, @Nullable UUID player, BlockPos pos) {
    if (level == null) {
      return;
    }
    var data = UIStateSavedData.of(level);
    if (data.remove(pos)) {
      PacketDistributor.sendToAllPlayers(new SUpdateUIStatePacket(level.dimension(), player, pos, null));
    }
  }

  @Override
  public Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> exportAll(Player player) {
    var server = player.level().getServer();
    if (server == null) {
      return new HashMap<>();
    }
    Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> all = new HashMap<>();
    for (var level : server.getAllLevels()) {
      var data = UIStateSavedData.of(level);
      if (data.isEmpty()) {
        continue;
      }
      all.put(level.dimension(), data.save());
    }
    return all;
  }

  @Override
  public void replaceAll(@Nullable Map<ResourceKey<Level>, Map<UUID, List<UIStateSavedData.Data>>> data) {}
}
