package es.degrassi.mmr.data.kubejs.recipeschema;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ListRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaData;
import dev.latvian.mods.kubejs.recipe.schema.function.SetFunction;
import dev.latvian.mods.kubejs.util.IntBounds;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.integration.kubejs.RecipeIdPostProcessor;
import es.degrassi.mmr.common.integration.kubejs.components.IdentifierComponent;
import es.degrassi.mmr.common.integration.kubejs.components.ProgressDataComponent;
import es.degrassi.mmr.common.integration.kubejs.components.RecipeRequirementComponent;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MMRRecipeSchemaProvider extends RecipeSchemaProvider {
  public MMRRecipeSchemaProvider(GatherDataEvent event) {
    super("MMRRecipeSchemaProvider", event);
  }

  @Override
  public void add(HolderLookup.Provider lookup) {
    add(ModularMachineryReborn.rl("machine_recipe"), builder -> builder
        .recipeFactory(ModularMachineryReborn.rl("machine_recipe"))
        .constructors(new RecipeSchemaData.ConstructorData(List.of("machine", "time"), Map.of()))
        .postProcessors(new RecipeIdPostProcessor())
        .function("hide", new SetFunction("hidden", new JsonPrimitive(true)))
        .keys(
            IdentifierComponent.IDENTIFIER.otherKey("machine"),
            TimeComponent.TICKS.otherKey("time"),
            ListRecipeComponent.create(
                RecipeRequirementComponent.RECIPE_REQUIREMENT,
                false,
                false,
                IntBounds.OPTIONAL,
                Optional.empty()
            ).otherKey("requirements").optional(List.of()).alwaysWrite().exclude(),
            ListRecipeComponent.create(
                RecipeRequirementComponent.RECIPE_REQUIREMENT,
                false,
                false,
                IntBounds.OPTIONAL,
                Optional.empty()
            ).otherKey("jei_requirements").optional(List.of()).alwaysWrite().exclude(),
            NumberComponent.INT.otherKey("priority").exclude().optional(0),
            BooleanComponent.BOOLEAN.otherKey("hidden").exclude().optional(false),
            BooleanComponent.BOOLEAN.otherKey("voidOnFailure").exclude().optional(true),
            BooleanComponent.BOOLEAN.otherKey("renderProgress").exclude().optional(true),
            ProgressDataComponent.PROGRESS_DATA.otherKey("progressData").exclude().defaultOptional(),
            NumberComponent.INT.otherKey("width").exclude().optional(256),
            NumberComponent.INT.otherKey("height").exclude().optional(256)
        )
    );
  }
}
