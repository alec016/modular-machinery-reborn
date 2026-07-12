package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.capability.IFuelHandler;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.Direction;
import es.degrassi.mmreborn.common.crafting.helper.FuelData;
import es.degrassi.mmreborn.common.crafting.helper.IDirectionalRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFuel;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.category.drawable.FuelDrawable;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.machine.component.FuelComponent;
import es.degrassi.mmreborn.common.util.Utils;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class JeiFuelComponent extends JeiComponent<Long, RecipeRequirement<FuelComponent, RequirementFuel, IFuelHandler>> implements IDirectionalRequirement {
  private final FuelDrawable progress;
  @Getter
  private final FuelData progressData;

  public JeiFuelComponent(RecipeRequirement<FuelComponent, RequirementFuel, IFuelHandler> requirement) {
    super(requirement, 0, 0);
    this.progressData = requirement.requirement().displayData();

    progress = new FuelDrawable(40, toJEIDirection(), progressData.getEmptyTexture(), progressData.getFillTexture());
  }

  @Override
  public ResourceLocation texture() {
    return ModularMachineryReborn.rl("textures/gui/widget/img.png");
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
  public List<Long> ingredients() {
    return Collections.singletonList(requirement.requirement().required);
  }

  @Override
  public void render(GuiGraphics guiGraphics, Long ingredient) {
    progress.draw(guiGraphics, 0, 0);
  }

  @Override
  public List<Component> getTooltip(Long ingredient, TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, tooltipFlag);
    tooltip.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.fuel",
            Utils.format(requirement.requirement().required)
        )
    );
    return tooltip;
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .setOverlay(MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(getWidth(), getHeight()), 0, 0)
        .setCustomRenderer(CustomIngredientTypes.LONG, this)
        .addIngredients(CustomIngredientTypes.LONG, ingredients());
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
