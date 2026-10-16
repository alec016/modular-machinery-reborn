package es.degrassi.mmr.common.integration.kubejs.builder;

import com.google.common.collect.Maps;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.MinBlocksPredicate;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;

import java.util.List;
import java.util.Map;

public class StructureBuilderJS {
  @HideFromJS
  private final Structure.Builder builder;
  @HideFromJS
  private List<List<String>> pattern;
  @HideFromJS
  private Map<Character, BlockIngredient> keys;
  private final Map<String, MinBlocksPredicate.MinMax> minBlocks;

  public static StructureBuilderJS create() {
    return new StructureBuilderJS(false);
  }
  public static StructureBuilderJS createRequirement() {
    return new StructureBuilderJS(true);
  }

  @HideFromJS
  private StructureBuilderJS(boolean requirement) {
    if (requirement) {
      builder = Structure.Builder.start('$');
    } else {
      builder = Structure.Builder.start('m');
    }
    minBlocks = Maps.newHashMap();
  }

  public StructureBuilderJS pattern(List<List<String>> pattern) {
    this.pattern = pattern;
    return this;
  }

  public StructureBuilderJS keys(Map<Character, BlockIngredient> keys) {
    this.keys = keys;
    return this;
  }

  public StructureBuilderJS addMinMaxBlock(String block, int min, int max) {
    this.minBlocks.put(block, new MinBlocksPredicate.MinMax(min, max));
    return this;
  }

  public StructureBuilderJS addMinBlock(String block, int min) {
    this.minBlocks.compute(block, (b, minmax) ->
      minmax == null ? MinBlocksPredicate.MinMax.min(min)
          : new MinBlocksPredicate.MinMax(min, minmax.max())
    );
    return this;
  }

  public StructureBuilderJS addMaxBlock(String block, int max) {
    this.minBlocks.compute(block, (b, minmax) ->
        minmax == null ? MinBlocksPredicate.MinMax.max(max)
            : new MinBlocksPredicate.MinMax(minmax.min(), max)
    );
    return this;
  }

  public StructureBuilderJS addExactBlock(String block, int number) {
    return addMinMaxBlock(block, number, number);
  }

  @HideFromJS
  public Structure build(List<ModifierReplacement> modifiers) {
    for (List<String> levels : pattern)
      builder.aisle(levels.toArray(new String[0]));
    for (Map.Entry<Character, BlockIngredient> key : keys.entrySet())
      builder.where(key.getKey(), key.getValue());
    return builder.build(pattern, keys, modifiers, minBlocks.isEmpty() ? MinBlocksPredicate.EMPTY : new MinBlocksPredicate(minBlocks));
  }

  @HideFromJS
  public Structure build() {
    return build(List.of());
  }
}
