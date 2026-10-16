package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementBiome;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public interface BiomeRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS biomes(List<Identifier> biomes) {
    return biomes(biomes, false);
  }

  default MachineRecipeBuilderJS biomes(List<Identifier> biomes, boolean blacklist) {
    return addRequirement(new RecipeRequirement<>(new RequirementBiome(biomes, blacklist)));
  }
}
