package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface FluidRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireFluid(SizedFluidIngredient stack, int x, int y) {
    return requireFluid(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, int x, int y) {
    return produceFluid(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS requireFluid(SizedFluidIngredient stack) {
    return requireFluid(stack, 1, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack) {
    return produceFluid(stack, 1, 0, 0);
  }

  default MachineRecipeBuilderJS requireFluid(SizedFluidIngredient stack, float chance, int x, int y) {
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 1");
    RequirementFluid requirement = new RequirementFluid(IOType.INPUT, stack, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement, chance, null));
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, float chance, int x, int y) {
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 1");
    RequirementFluid requirement = new RequirementFluid(IOType.OUTPUT, new SizedFluidIngredient(FluidIngredient.of(stack), stack.getAmount()), new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement, chance, null));
  }

  default MachineRecipeBuilderJS requireFluid(SizedFluidIngredient stack, float chance) {
    return requireFluid(stack, chance, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, float chance) {
    return produceFluid(stack, chance, 0, 0);
  }
}
