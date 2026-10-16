package es.degrassi.mmr.api;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class MinBlocksPredicate implements BiPredicate<BlockIngredient, BlockInWorld> {
  public static final NamedCodec<MinBlocksPredicate> CODEC = NamedCodec.unboundedMap(NamedCodec.STRING, MinMax.CODEC, "Map<String, MinMax>")
      .comapFlatMap(map -> DataResult.success(new MinBlocksPredicate(map)), MinBlocksPredicate::originals, "MinBlocksPredicate");

  public static final MinBlocksPredicate EMPTY = new MinBlocksPredicate(Maps.newHashMap());

  private final Map<BlockIngredient, MinMax> minBlocks;
  private final Map<String, MinMax> originals;
  @Getter
  private final Object2IntOpenHashMap<BlockIngredient> tests = new Object2IntOpenHashMap<>();
  @Getter
  private final List<Component> errors = new ArrayList<>();

  public MinBlocksPredicate(Map<String, MinMax> minBlocks) {
    this.minBlocks = Maps.newHashMap();
    minBlocks.forEach((id, minmax) -> {
      try {
        var bi = BlockIngredient.of(id);
        this.minBlocks.put(bi, minmax);
      } catch(Exception ignored) {}
    });
    this.originals = minBlocks;
    this.tests.defaultReturnValue(0);
  }

  private Map<String, MinMax> originals() {
    return this.originals;
  }

  public Map<BlockIngredient, MinMax> minBlocks() {
    return minBlocks;
  }

  public void reset() {
    tests.replaceAll((b, i) -> 0);
    errors.clear();
  }

  private Optional<Component> compute(BlockInWorld block) {
    for (var entry : minBlocks.entrySet()) {
      var key = entry.getKey();
      var value = entry.getValue();
      if (key.test(block)) {
        tests.addTo(key, 1);
        var found = tests.getInt(key);
        if (!value.test(found)) return Optional.of(value.errorMessage(found, key.getNamesUnified()));
        else return Optional.empty();
      }
    }
    return Optional.empty();
  }

  @Override
  public boolean test(BlockIngredient blockIngredient, BlockInWorld blockInWorld) {
    if (!blockIngredient.test(blockInWorld)) return false;
    return compute(blockInWorld).map(pair -> {
      errors.add(pair);
      return false;
    }).orElse(true);
  }

  public JsonElement asJson() {
    JsonObject json = new JsonObject();
    originals.forEach((block, minmax) -> json.add(block, minmax.toJson()));
    return json;
  }

  @Override
  public String toString() {
    JsonObject json = new JsonObject();
    minBlocks.forEach((block, number) -> {
      var string = block.getString();
      JsonObject object = number.toJson();
      object.addProperty("test", tests.getInt(block));
      json.add(string, object);
    });
    return new GsonBuilder().setPrettyPrinting().create().toJson(json);
  }

  public record MinMax(int min, int max) implements Predicate<Integer> {
    private static final int minValue = Integer.MIN_VALUE;
    private static final int maxValue = Integer.MAX_VALUE;
    public static final NamedCodec<MinMax> CODEC = NamedCodec.record(instance -> instance.group(
        NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min", minValue).forGetter(MinMax::min),
        NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max", maxValue).forGetter(MinMax::max)
    ).apply(instance, MinMax::new), "MinMax");

    public static MinMax max(int max) {
      return new MinMax(minValue, max);
    }

    public static MinMax min(int min) {
      return new MinMax(min, maxValue);
    }

    public boolean test(Integer toTest) {
      return min <= toTest && toTest <= max;
    }

    public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("min", min);
      json.addProperty("max", max);
      return json;
    }

    public MutableComponent guiText() {
      if (min == minValue && max == maxValue) return Component.empty();
      if (min == max) return Component.translatable("mmr.controller.exactly", min);
      if (min == minValue) return Component.translatable("mmr.controller.max", max);
      if (max == maxValue) return Component.translatable("mmr.controller.min", min);
      return Component.translatable("mmr.controller.min_max", min, max);
    }

    public Component errorMessage(int found, Component args) {
      if (min == max) return Component.translatable(
          "mmr.structure.error.exact",
          min,
          found,
          args
      );
      if (min == minValue) return Component.translatable(
          "mmr.structure.error.max",
          max,
          found,
          args
      );
      if (max == maxValue) return Component.translatable(
          "mmr.structure.error.min",
          min,
          found,
          args
      );
      return Component.translatable(
          "mmr.structure.error.between",
          min,
          max,
          found,
          args
      );
    }
  }
}
