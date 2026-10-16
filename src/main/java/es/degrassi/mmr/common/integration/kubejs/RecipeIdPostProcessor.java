package es.degrassi.mmr.common.integration.kubejs;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessorType;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.registration.RecipeRegistration;
import es.degrassi.mmr.common.util.MMRLogger;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RecipeIdPostProcessor implements RecipePostProcessor {
  public static final RecipePostProcessorType<RecipeIdPostProcessor> TYPE =
      new RecipePostProcessorType<>(ModularMachineryReborn.rl("recipe_id"), MapCodec.unit(new RecipeIdPostProcessor()));

  @HideFromJS
  public static final Map<Identifier, Map<Identifier, Integer>> IDS = Maps.newHashMap();

  @Override
  public RecipePostProcessorType<RecipeIdPostProcessor> type() {
    return TYPE;
  }

  @Override
  public void process(RecipeValidationContext ctx, KubeRecipe recipe) {
    if (!recipe.newRecipe || !(recipe instanceof MachineRecipeBuilderJS)) return;
    for (RecipeComponentValue<?> value : recipe.getRecipeComponentValues()) {
      if (value.key.name.equals("machine") && value.value instanceof Identifier machine) {
        int uniqueID = IDS.computeIfAbsent(RecipeRegistration.RECIPE_TYPE.getId(), id -> new HashMap<>()).computeIfAbsent(machine, m -> 0);
        IDS.get(RecipeRegistration.RECIPE_TYPE.getId()).put(machine, uniqueID + 1);
        recipe.id = Identifier.fromNamespaceAndPath("kubejs",
            RecipeRegistration.RECIPE_TYPE.getId().getPath() + "/" + machine.getNamespace() + "/" + machine.getPath() + "/" + uniqueID);
        MMRLogger.INSTANCE.info("Built recipe with id: {}", recipe.id);
      }
    }
  }
}
