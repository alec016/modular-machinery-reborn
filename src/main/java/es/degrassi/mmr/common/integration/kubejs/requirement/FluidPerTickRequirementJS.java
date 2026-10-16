package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementFluidPerTick;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface FluidPerTickRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireFluidPerTick(SizedFluidIngredient stack, int x, int y) {
    return requireFluidPerTick(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS produceFluidPerTick(FluidStack stack, int x, int y) {
    return produceFluidPerTick(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS requireFluidPerTick(SizedFluidIngredient stack) {
    return requireFluidPerTick(stack, 1, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluidPerTick(FluidStack stack) {
    return produceFluidPerTick(stack, 1, 0, 0);
  }

  default MachineRecipeBuilderJS requireFluidPerTick(SizedFluidIngredient stack, float chance, int x, int y) {
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 1");
    RequirementFluidPerTick requirement = new RequirementFluidPerTick(IOType.INPUT, stack, new PositionedRequirement(x,
        y));
    return addRequirement(new RecipeRequirement<>(requirement, chance, null));
  }

  default MachineRecipeBuilderJS produceFluidPerTick(FluidStack stack, float chance, int x, int y) {
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 1");
    RequirementFluidPerTick requirement = new RequirementFluidPerTick(IOType.OUTPUT, new SizedFluidIngredient(FluidIngredient.of(stack), stack.getAmount()), new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement, chance, null));
  }

  default MachineRecipeBuilderJS requireFluidPerTick(SizedFluidIngredient stack, float chance) {
    return requireFluidPerTick(stack, chance, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluidPerTick(FluidStack stack, float chance) {
    return produceFluidPerTick(stack, chance, 0, 0);
  }
}
