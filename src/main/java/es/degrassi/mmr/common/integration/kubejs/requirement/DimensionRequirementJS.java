package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public interface DimensionRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS dimensions(List<Identifier> dimensions) {
    return dimensions(dimensions, false);
  }

  default MachineRecipeBuilderJS dimensions(List<Identifier> dimensions, boolean blacklist) {
    return addRequirement(new RecipeRequirement<>(new RequirementDimension(dimensions, blacklist)));
  }
}
