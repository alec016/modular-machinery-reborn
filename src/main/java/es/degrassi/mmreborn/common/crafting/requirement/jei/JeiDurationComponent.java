package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.Direction;
import es.degrassi.mmreborn.common.crafting.helper.IDirectionalRequirement;
import es.degrassi.mmreborn.common.crafting.helper.ProgressData;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDuration;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.category.drawable.ProgressDrawable;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.machine.component.DurationComponent;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class JeiDurationComponent extends JeiComponent<Integer, RecipeRequirement<DurationComponent,
    RequirementDuration, Void>>
implements IDirectionalRequirement {
  private final ProgressDrawable progress;
  @Getter
  private final ProgressData progressData;
  private int duration;
  public JeiDurationComponent(RecipeRequirement<DurationComponent, RequirementDuration, Void> requirement,
                              int ticksPerCycle, ProgressData data) {
    super(requirement, 0, 0);
    this.progressData = data;

    progress = new ProgressDrawable(ticksPerCycle, toJEIDirection(), progressData.getEmptyTexture(), progressData.getFillTexture());
  }

  @Override
  public Direction getDirection() {
    return progressData.direction();
  }

  @Override
  public int getWidth() {
    return progress.getWidth();
  }

  @Override
  public int getHeight() {
    return progress.getHeight();
  }

  @Override
  public List<Integer> ingredients() {
    return List.of(duration);
  }

  @Override
  public void render(GuiGraphics guiGraphics, Integer ingredient) {
    progress.draw(guiGraphics, 0, 0);
  }

  @Override
  public List<Component> getTooltip(Integer ingredient, TooltipFlag tooltipFlag) {
   List<Component> tooltip = super.getTooltip(ingredient, tooltipFlag);
   tooltip.add(Component.translatable(
       "modular_machinery_reborn.jei.ingredient.duration",
       duration
   ));
   return tooltip;
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    this.duration = recipe.getRecipeTotalTickTime();
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .setOverlay(MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(getWidth(), getHeight()), 0, 0)
        .setCustomRenderer(CustomIngredientTypes.INTEGER, this)
        .addIngredients(CustomIngredientTypes.INTEGER, ingredients());
  }

  private IDrawableAnimated.StartDirection toJEIDirection() {
    return switch (getDirection()) {
      case LEFT -> IDrawableAnimated.StartDirection.LEFT;
      case RIGHT -> IDrawableAnimated.StartDirection.RIGHT;
      case TOP -> IDrawableAnimated.StartDirection.TOP;
      case BOTTOM -> IDrawableAnimated.StartDirection.BOTTOM;
    };
  }
}
