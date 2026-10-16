package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementExperience;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;

public interface ExperienceRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireExperience(long amount) {
    return addRequirement(new RecipeRequirement<>(new RequirementExperience(IOType.INPUT, amount)));
  }

  default MachineRecipeBuilderJS produceExperience(long amount) {
    return addRequirement(new RecipeRequirement<>(new RequirementExperience(IOType.OUTPUT, amount)));
  }
}
