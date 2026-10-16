package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementChunkload;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;

public interface ChunkloadRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS chunkload(Integer radius) {
    if (radius < 1)
      return error("ChunkloadComponent radius can no be less than 1: \"{}\"", radius);
    return this.addRequirement(new RecipeRequirement<>(new RequirementChunkload(radius)));
  }
}
