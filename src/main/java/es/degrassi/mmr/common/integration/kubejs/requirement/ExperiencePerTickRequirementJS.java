package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementExperiencePerTick;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;

public interface ExperiencePerTickRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireExperiencePerTick(long amount) {
    return addRequirement(new RecipeRequirement<>(new RequirementExperiencePerTick(IOType.INPUT, amount)));
  }

  default MachineRecipeBuilderJS produceExperiencePerTick(long amount) {
    return addRequirement(new RecipeRequirement<>(new RequirementExperiencePerTick(IOType.OUTPUT, amount)));
  }
}
