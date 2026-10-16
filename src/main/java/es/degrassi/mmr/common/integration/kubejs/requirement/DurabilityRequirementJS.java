package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementDurability;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;
import net.minecraft.world.item.crafting.Ingredient;

public interface DurabilityRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS damageItem(Ingredient ingredient, int amount, int x, int y) {
    RequirementDurability requirement = new RequirementDurability(IOType.INPUT, ingredient, amount, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement));
  }

  default MachineRecipeBuilderJS repairItem(Ingredient ingredient, int amount, int x, int y) {
    RequirementDurability requirement = new RequirementDurability(IOType.OUTPUT, ingredient, amount, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement));
  }

  default MachineRecipeBuilderJS damageItem(Ingredient ingredient) {
    return damageItem(ingredient, 1, 0, 0);
  }

  default MachineRecipeBuilderJS damageItem(Ingredient ingredient, int amount) {
    return damageItem(ingredient, amount, 0, 0);
  }

  default MachineRecipeBuilderJS damageItem(Ingredient ingredient, int x, int y) {
    return damageItem(ingredient, 1, x, y);
  }

  default MachineRecipeBuilderJS repairItem(Ingredient ingredient, int amount) {
    return repairItem(ingredient, amount, 0, 0);
  }

  default MachineRecipeBuilderJS repairItem(Ingredient ingredient) {
    return repairItem(ingredient, 1, 0, 0);
  }

  default MachineRecipeBuilderJS repairItem(Ingredient ingredient, int x, int y) {
    return repairItem(ingredient, 1, x, y);
  }
}
