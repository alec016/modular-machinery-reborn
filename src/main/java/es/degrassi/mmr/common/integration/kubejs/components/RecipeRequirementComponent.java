package es.degrassi.mmr.common.integration.kubejs.components;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import net.minecraft.resources.ResourceKey;

public record RecipeRequirementComponent(ResourceKey<RecipeComponentType<?>> type) implements RecipeComponent<RecipeRequirement<?, ?, ?>> {
  public static final RecipeRequirementComponent RECIPE_REQUIREMENT = new RecipeRequirementComponent(RecipeComponentType.key(ModularMachineryReborn.rl("requirements")));
  @Override
  public Codec<RecipeRequirement<?, ?, ?>> codec() {
    return RecipeRequirement.CODEC.codec();
  }

  @Override
  public TypeInfo typeInfo() {
    return TypeInfo.of(RecipeRequirement.class);
  }
}
