package es.degrassi.mmr.common.integration.kubejs.requirement;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementLootTable;
import es.degrassi.mmr.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmr.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.resources.Identifier;

public interface LootTableRequirementJS extends RecipeJSBuilder {
  default MachineRecipeBuilderJS lootTable(Identifier lootTable) {
    return lootTable(lootTable, 0, 0, 0);
  }

  default MachineRecipeBuilderJS lootTable(Identifier lootTable, float luck) {
    return lootTable(lootTable, luck, 0, 0);
  }

  default MachineRecipeBuilderJS lootTable(Identifier lootTable, int x, int y) {
    return lootTable(lootTable, 0, x, y);
  }

  default MachineRecipeBuilderJS lootTable(Identifier lootTable, float luck, int x, int y) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementLootTable(lootTable, luck, new PositionedRequirement(x, y))));
  }
}
