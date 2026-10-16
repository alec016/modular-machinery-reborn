package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementEmpty;
import es.degrassi.mmr.common.integration.jei.JeiEmptyRequirementRegistry;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.machine.component.EmptyComponent;
import es.degrassi.mmr.common.util.EmptyRequirementType;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;

public class JeiEmptyComponent extends JeiComponent<Void, RecipeRequirement<EmptyComponent, RequirementEmpty, Void>> {
  private final EmptyRequirementType type;
  private int width;
  private int height;

  public JeiEmptyComponent(RecipeRequirement<EmptyComponent, RequirementEmpty, Void> requirement) {
    super(
        requirement,
        requirement.requirement().getRequirementType().getUOffset(),
        requirement.requirement().getRequirementType().getVOffset()
    );
    this.type = requirement.requirement().getRequirementType();
    this.width = requirement.requirement().getRequirementType().getWidth();
    this.height = requirement.requirement().getRequirementType().getHeight();
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, Void ingredient) {
    width += 2;
    height += 2;
    super.render(GuiGraphicsExtractor, ingredient);
    width -= 2;
    height -= 2;
  }

  @Override
  public List<Void> ingredients() {
    return List.of();
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    if (JeiEmptyRequirementRegistry.hasJeiConsumer(type)) {
      JeiEmptyRequirementRegistry.getConsumer(type).execute(this, category, builder, recipe, focuses);
    }
  }

  @Override
  public RecipeIngredientRole role() {
    return RecipeIngredientRole.RENDER_ONLY;
  }
}
