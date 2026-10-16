package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementItem;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface ItemRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireItem(SizedIngredient stack) {
    return requireItem(stack, 0, 0);
  }

  default MachineRecipeBuilderJS produceItem(SizedIngredient stack) {
    return produceItem(stack, 0, 0);
  }

  default MachineRecipeBuilderJS requireItem(SizedIngredient stack, float chance) {
    return requireItem(stack, chance, 0, 0);
  }

  default MachineRecipeBuilderJS produceItem(SizedIngredient stack, float chance) {
    return produceItem(stack, chance, 0, 0);
  }

  default MachineRecipeBuilderJS requireItem(SizedIngredient stack, int x, int y) {
    return requireItem(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS produceItem(SizedIngredient stack, int x, int y) {
    return produceItem(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS requireItem(SizedIngredient stack, float chance, int x, int y) {
    if(stack.ingredient().getValues().size() == 0)
      return this.error("Invalid empty ingredient in item input requirement");
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 1");
    RequirementItem requirement = new RequirementItem(IOType.INPUT, stack, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement, chance, null));
  }

  default MachineRecipeBuilderJS produceItem(SizedIngredient stack, float chance, int x, int y) {
    var length = stack.ingredient().getValues().size();
    if(length == 0)
      return this.error("Invalid empty item in item output requirement");
    if (length > 1)
      return this.error("Item Requirement cant use tags or multiple outputs");
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 1");
    RequirementItem requirement = new RequirementItem(IOType.OUTPUT, stack, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement, chance, null));
  }
}
