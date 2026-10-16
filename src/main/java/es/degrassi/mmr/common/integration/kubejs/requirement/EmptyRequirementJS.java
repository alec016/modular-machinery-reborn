package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.registration.EmptyRequirementTypeRegistration;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementEmpty;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;

public interface EmptyRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS emptyItem(int x, int y) {
    if (!isJei()) return error("Empty Item Requirement can only be used after .jei() call");
    return addRequirement(new RecipeRequirement<>(new RequirementEmpty(EmptyRequirementTypeRegistration.ITEM.get(), new PositionedRequirement(x, y))));
  }
  default MachineRecipeBuilderJS emptyItem() {
    return emptyItem(0, 0);
  }

  default MachineRecipeBuilderJS emptyEnergy(int x, int y) {
    if (!isJei()) return error("Empty Energy Requirement can only be used after .jei() call");
    return addRequirement(new RecipeRequirement<>(new RequirementEmpty(EmptyRequirementTypeRegistration.ENERGY.get(), new PositionedRequirement(x, y))));
  }
  default MachineRecipeBuilderJS emptyEnergy() {
    return emptyEnergy(0, 0);
  }

  default MachineRecipeBuilderJS emptyFluid(int x, int y) {
    if (!isJei()) return error("Empty Fluid Requirement can only be used after .jei() call");
    return addRequirement(new RecipeRequirement<>(new RequirementEmpty(EmptyRequirementTypeRegistration.FLUID.get(), new PositionedRequirement(x, y))));
  }
  default MachineRecipeBuilderJS emptyFluid() {
    return emptyFluid(0, 0);
  }
}
