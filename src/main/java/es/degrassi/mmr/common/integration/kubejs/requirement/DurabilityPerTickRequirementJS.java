package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementDurabilityPerTick;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmr.common.machine.IOType;
import net.minecraft.world.item.crafting.Ingredient;

public interface DurabilityPerTickRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS damageItemPerTick(Ingredient ingredient, int amount, int x, int y) {
    RequirementDurabilityPerTick requirement = new RequirementDurabilityPerTick(IOType.INPUT, ingredient, amount, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement));
  }

  default MachineRecipeBuilderJS repairItemPerTick(Ingredient ingredient, int amount, int x, int y) {
    RequirementDurabilityPerTick requirement = new RequirementDurabilityPerTick(IOType.OUTPUT, ingredient, amount, new PositionedRequirement(x, y));
    return addRequirement(new RecipeRequirement<>(requirement));
  }

  default MachineRecipeBuilderJS damageItemPerTick(Ingredient ingredient) {
    return damageItemPerTick(ingredient, 1, 0, 0);
  }

  default MachineRecipeBuilderJS damageItemPerTick(Ingredient ingredient, int amount) {
    return damageItemPerTick(ingredient, amount, 0, 0);
  }

  default MachineRecipeBuilderJS damageItemPerTick(Ingredient ingredient, int x, int y) {
    return damageItemPerTick(ingredient, 1, x, y);
  }

  default MachineRecipeBuilderJS repairItemPerTick(Ingredient ingredient, int amount) {
    return repairItemPerTick(ingredient, amount, 0, 0);
  }

  default MachineRecipeBuilderJS repairItemPerTick(Ingredient ingredient) {
    return repairItemPerTick(ingredient, 1, 0, 0);
  }

  default MachineRecipeBuilderJS repairItemPerTick(Ingredient ingredient, int x, int y) {
    return repairItemPerTick(ingredient, 1, x, y);
  }
}
