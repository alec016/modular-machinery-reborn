package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementRedstone;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;

public interface RedstoneRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS requireRedstone(int amount) {
    if (amount < 0 || amount > 15) return error("Amount must be between [0,15], found: {}", amount);
    return addRequirement(new RecipeRequirement<>(new RequirementRedstone(amount, IOType.INPUT)));
  }

  default MachineRecipeBuilderJS emitRedstone(int amount) {
    if (amount < 0 || amount > 15) return error("Amount must be between [0,15], found: {}", amount);
    return addRequirement(new RecipeRequirement<>(new RequirementRedstone(amount, IOType.OUTPUT)));
  }
}
