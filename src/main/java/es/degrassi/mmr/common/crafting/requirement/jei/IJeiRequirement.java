package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

public interface IJeiRequirement<R extends RecipeRequirement<?, ?, ?>> {
  void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses);

  R getRequirement();

  default PositionedRequirement getPosition() {
    return getRequirement().requirement().getPosition();
  }
}
