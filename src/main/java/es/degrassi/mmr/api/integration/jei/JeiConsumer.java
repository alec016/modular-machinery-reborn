package es.degrassi.mmr.api.integration.jei;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementEmpty;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiComponent;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.machine.component.EmptyComponent;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

@FunctionalInterface
public interface JeiConsumer {
  void execute(JeiComponent<Void, RecipeRequirement<EmptyComponent, RequirementEmpty, Void>> component, MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses);
}
