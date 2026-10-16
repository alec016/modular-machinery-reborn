package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.helper.Direction;
import es.degrassi.mmr.common.crafting.helper.IDirectionalRequirement;
import es.degrassi.mmr.common.crafting.helper.ProgressData;
import es.degrassi.mmr.common.crafting.requirement.RequirementDuration;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.integration.jei.category.drawable.ProgressDrawable;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Duration;
import es.degrassi.mmr.common.machine.component.DurationComponent;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;

public class JeiDurationComponent extends JeiComponent<Duration, RecipeRequirement<DurationComponent, RequirementDuration, Void>> implements IDirectionalRequirement {
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
  public List<Duration> ingredients() {
    return List.of(new Duration(duration));
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, Duration ingredient) {
    progress.draw(GuiGraphicsExtractor, 0, 0);
  }

  @Override
  public @NonNull List<Component> getTooltip(Duration ingredient, Item.TooltipContext context, @Nullable Player player,
                                             TooltipFlag tooltipFlag) {
   List<Component> tooltip = super.getTooltip(ingredient, context, player, tooltipFlag);
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
        .setCustomRenderer(CustomIngredientTypes.DURATION, this)
        .addIngredients(CustomIngredientTypes.DURATION, ingredients());
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
