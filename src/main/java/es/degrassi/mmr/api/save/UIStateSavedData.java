package es.degrassi.mmr.api.save;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.UnboundedMapCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.uistate.UIState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class UIStateSavedData extends SavedData {
  public static final NamedCodec<UUID> UUID_CODEC = NamedCodec.STRING.xmap(UUID::fromString, UUID::toString, "UUID");
  public static final NamedCodec<Optional<UUID>> OPTIONAL_UUID_CODEC = UUID_CODEC.optionalFieldOf("optional");

  public static final NamedCodec<Map<UUID, List<Data>>> MAP_CODEC = UnboundedMapCodec.of(UUID_CODEC, Data.CODEC.listOf(), "Data list by player uuid codec");

  public static final NamedCodec<UIStateSavedData> FINAL_CODEC = MAP_CODEC.xmap(UIStateSavedData::new, UIStateSavedData::data, "UIStateSavedData");

  public static final UnboundedMapCodec<ResourceKey<Level>, Map<UUID, List<Data>>> FULL_CODEC = NamedCodec.unboundedMap(
      NamedCodec.of(ResourceKey.codec(Registries.DIMENSION)),
      MAP_CODEC,
      "FullCodec"
  );

  public static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceKey<Level>, Map<UUID, List<Data>>>> STREAM_CODEC = FULL_CODEC.streamCodec();

  public static final SavedDataType<UIStateSavedData> TYPE = new SavedDataType<>(
      ModularMachineryReborn.rl("ui_states"),
      UIStateSavedData::new,
      FINAL_CODEC.codec()
  );

  private final Map<UUID, List<Data>> data = new HashMap<>();

  public UIStateSavedData() {
    setDirty();
  }

  private UIStateSavedData(Map<UUID, List<Data>> data) {
    data.forEach((uuid, d) -> this.data.put(uuid, new ArrayList<>(d)));
  }

  public Map<UUID, List<Data>> data() {
    return data;
  }

  public Map<UUID, List<Data>> save() {
    var map = new HashMap<UUID, List<Data>>();
    data.forEach((uuid, d) -> map.put(uuid, new ArrayList<>(d)));
    return map;
  }

  public static UIStateSavedData of(ServerLevel level) {
    return level.getDataStorage().computeIfAbsent(TYPE);
  }

  public boolean isEmpty() {
    return data.isEmpty();
  }

  public void put(Player player, BlockPos pos, UIState tag) {
    remove(player, pos);
    data.computeIfAbsent(player.getUUID(), _ -> new ArrayList<>()).add(new Data(pos, tag));
    setDirty();
  }

  public void remove(Player player, BlockPos pos) {
    var removed = data.get(player.getUUID()).removeIf(d -> d.pos.equals(pos));
    if (removed) {
      setDirty();
    }
  }

  /** Removes the state of a position for every player (the block it belonged to is gone). */
  public boolean remove(BlockPos pos) {
    boolean removed = data.values().stream().anyMatch(list -> list.removeIf(d -> d.pos.equals(pos)));
    if (removed) {
      setDirty();
    }
    return removed;
  }

  public record Data(BlockPos pos, UIState data) {
    public static final NamedCodec<Data> CODEC = NamedCodec.record(i -> i.group(
        NamedCodec.of(BlockPos.CODEC).fieldOf("pos").forGetter(Data::pos),
        UIState.CODEC.fieldOf("data").forGetter(Data::data)
    ).apply(i, Data::new), "UIStateSavedData$Data");
  }
}
