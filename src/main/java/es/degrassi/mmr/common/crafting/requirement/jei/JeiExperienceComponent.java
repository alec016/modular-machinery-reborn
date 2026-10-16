package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.experiencelib.api.ExperienceStack;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.api.xei.jei.IngredientTypes;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementExperience;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.machine.component.ExperienceComponent;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

import java.util.List;

public class JeiExperienceComponent extends JeiComponent<ExperienceStack, RecipeRequirement<ExperienceComponent, RequirementExperience, IExperienceHandler>> {
  public JeiExperienceComponent(RecipeRequirement<ExperienceComponent, RequirementExperience, IExperienceHandler> requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public List<ExperienceStack> ingredients() {
    return List.of(new ExperienceStack(requirement.requirement().getRequired()));
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder.addInvisibleIngredients(role())
        .addIngredients(IngredientTypes.EXPERIENCE, ingredients());
  }
}
