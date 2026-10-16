package es.degrassi.mmr.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.List;
import java.util.Map;

public class Pattern {
  private final List<List<String>> strings;
  private final Map<Character, BlockIngredient> keys;

  private final Map<BlockPos, BlockIngredient> pattern;
  private final Map<BlockPos, BlockIngredient> pattern_north;
  private final Map<BlockPos, BlockIngredient> pattern_south;
  private final Map<BlockPos, BlockIngredient> pattern_east;
  private final Map<BlockPos, BlockIngredient> pattern_west;

  @Getter
  private final List<ModifierReplacement> modifiers;
  private final Map<BlockPos, List<ModifierReplacement>> modifiers_north;
  private final Map<BlockPos, List<ModifierReplacement>> modifiers_south;
  private final Map<BlockPos, List<ModifierReplacement>> modifiers_east;
  private final Map<BlockPos, List<ModifierReplacement>> modifiers_west;

  public Pattern(Map<BlockPos, BlockIngredient> pattern, List<List<String>> strings, Map<Character, BlockIngredient> keys) {
    this.pattern = pattern;
    this.strings = strings;
    this.keys = keys;
    this.pattern_north = rotate(Rotation.NONE);
    this.pattern_south = rotate(Rotation.CLOCKWISE_180);
    this.pattern_west = rotate(Rotation.COUNTERCLOCKWISE_90);
    this.pattern_east = rotate(Rotation.CLOCKWISE_90);
    this.modifiers = Lists.newArrayList();
    this.modifiers_north = Maps.newHashMap();
    this.modifiers_south = Maps.newHashMap();
    this.modifiers_east = Maps.newHashMap();
    this.modifiers_west = Maps.newHashMap();
  }

  public Pattern(Map<BlockPos, BlockIngredient> pattern, List<List<String>> strings,
                 Map<Character, BlockIngredient> keys, List<ModifierReplacement> modifiers) {
    this.pattern = pattern;
    this.strings = strings;
    this.keys = keys;
    this.pattern_north = rotate(Rotation.NONE);
    this.pattern_south = rotate(Rotation.CLOCKWISE_180);
    this.pattern_west = rotate(Rotation.COUNTERCLOCKWISE_90);
    this.pattern_east = rotate(Rotation.CLOCKWISE_90);
    this.modifiers = modifiers;
    this.modifiers_north = rotateModifiers(Rotation.NONE);
    this.modifiers_south = rotateModifiers(Rotation.CLOCKWISE_180);
    this.modifiers_west = rotateModifiers(Rotation.COUNTERCLOCKWISE_90);
    this.modifiers_east = rotateModifiers(Rotation.CLOCKWISE_90);

    modifiers_north.forEach((pos, modifierList) -> modifierList
        .forEach(modifier -> pattern_north
            .put(pos, modifier.getIngredient().copyWithRotation(Rotation.NONE).merge(pattern_north.get(pos)))));

    modifiers_south.forEach((pos, modifierList) -> modifierList
        .forEach(modifier -> pattern_south
            .put(pos, modifier.getIngredient().copyWithRotation(Rotation.CLOCKWISE_180).merge(pattern_south.get(pos)))));

    modifiers_west.forEach((pos, modifierList) -> modifierList
        .forEach(modifier -> pattern_west
            .put(pos, modifier.getIngredient().copyWithRotation(Rotation.COUNTERCLOCKWISE_90).merge(pattern_west.get(pos)))));

    modifiers_east.forEach((pos, modifierList) -> modifierList
        .forEach(modifier -> pattern_east
            .put(pos, modifier.getIngredient().copyWithRotation(Rotation.CLOCKWISE_90).merge(pattern_east.get(pos)))));
  }

  public Map<BlockPos, BlockIngredient> get(Direction direction) {
    return switch (direction) {
      case WEST -> pattern_west;
      case EAST -> pattern_east;
      case SOUTH -> pattern_south;
      default -> pattern_north;
    };
  }

  private Rotation fromDirection(Direction direction) {
    return switch (direction) {
      case EAST -> Rotation.CLOCKWISE_90;
      case WEST -> Rotation.COUNTERCLOCKWISE_90;
      case SOUTH -> Rotation.CLOCKWISE_180;
      default -> Rotation.NONE;
    };
  }

  public Map<BlockPos, BlockIngredient> getFiltered(Direction direction) {
    return rotate(fromDirection(direction));
  }

  public Map<BlockPos, List<ModifierReplacement>> getModifiers(Direction direction) {
    return switch (direction) {
      case WEST -> modifiers_west;
      case EAST -> modifiers_east;
      case SOUTH -> modifiers_south;
      default -> modifiers_north;
    };
  }

  public Map<BlockPos, List<ModifierReplacement>> rotateModifiers(Rotation rotation) {
    Map<BlockPos, List<ModifierReplacement>> map = Maps.newHashMap();
    modifiers.forEach(modifier -> map
        .computeIfAbsent(modifier.getPosition().rotate(rotation), key -> Lists.newArrayList()).add(modifier));
    return map;
  }

  private Map<BlockPos, BlockIngredient> rotate(Rotation rotation) {
    Map<BlockPos, BlockIngredient> rotated = Maps.newHashMap();
    pattern.forEach((pos, ingredient) -> rotated.put(pos.rotate(rotation), ingredient.copyWithRotation(rotation)));
    return rotated;
  }

  public List<List<String>> asList() {
    return strings;
  }

  public Map<Character, BlockIngredient> asMap() {
    return keys;
  }

  public boolean match(LevelReader world, BlockPos machinePos, Direction machineFacing, MinBlocksPredicate predicate) {
    Map<BlockPos, BlockIngredient> blocks = get(machineFacing);
    BlockPos.MutableBlockPos worldPos = new BlockPos.MutableBlockPos();
    for (BlockPos pos : blocks.keySet()) {
      BlockIngredient ingredient = blocks.get(pos);
      worldPos.set(pos.getX() + machinePos.getX(), pos.getY() + machinePos.getY(), pos.getZ() + machinePos.getZ());
      BlockInWorld info = new BlockInWorld(world, worldPos, false);
      if (!predicate.test(ingredient, info)) return false;
    }
    for (var entry : predicate.getTests().object2IntEntrySet()) {
      var ing = entry.getKey();
      var found = entry.getIntValue();
      var minmax = predicate.minBlocks().get(ing);
      if (!minmax.test(found)) {
        predicate.getErrors().add(minmax.errorMessage(found, ing.getNamesUnified()));
        return false;
      }
    }
    return true;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    JsonArray keysList = new JsonArray();
    strings.forEach(list -> {
      JsonArray keyKeyList = new JsonArray();
      list.forEach(keyKeyList::add);
      keysList.add(keyKeyList);
    });
    json.add("strings", keysList);
    JsonObject keys = new JsonObject();
    this.keys.forEach((key, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      keys.add(key + "", states);
    });
    json.add("keys", keys);
    JsonObject pattern = new JsonObject();
    this.pattern.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern.add(pos.toString(), states);
    });
    json.add("pattern", pattern);
    JsonObject pattern_north = new JsonObject();
    this.pattern_north.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_north.add(pos.toString(), states);
    });
    json.add("pattern_north", pattern_north);
    JsonObject pattern_south = new JsonObject();
    this.pattern_south.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_south.add(pos.toString(), states);
    });
    json.add("pattern_south", pattern_south);
    JsonObject pattern_east = new JsonObject();
    this.pattern_east.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_east.add(pos.toString(), states);
    });
    json.add("pattern_east", pattern_east);
    JsonObject pattern_west = new JsonObject();
    this.pattern_west.forEach((pos, ingredient) -> {
      List<PartialBlockState> all = ingredient.getAll();
      JsonArray states = new JsonArray();
      all.forEach(state -> states.add(state.toString()));
      pattern_west.add(pos.toString(), states);
    });
    json.add("pattern_west", pattern_west);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
