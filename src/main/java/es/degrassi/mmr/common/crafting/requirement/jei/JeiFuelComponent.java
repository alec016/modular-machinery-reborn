package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.capability.IFuelHandler;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.helper.Direction;
import es.degrassi.mmr.common.crafting.helper.FuelData;
import es.degrassi.mmr.common.crafting.helper.IDirectionalRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementFuel;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.integration.jei.category.drawable.FuelDrawable;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Fuel;
import es.degrassi.mmr.common.machine.component.FuelComponent;
import es.degrassi.mmr.common.util.Utils;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class JeiFuelComponent extends JeiComponent<Fuel, RecipeRequirement<FuelComponent, RequirementFuel, IFuelHandler>> implements IDirectionalRequirement {
  private final FuelDrawable progress;
  @Getter
  private final FuelData progressData;

  public JeiFuelComponent(RecipeRequirement<FuelComponent, RequirementFuel, IFuelHandler> requirement) {
    super(requirement, 0, 0);
    this.progressData = requirement.requirement().displayData();

    progress = new FuelDrawable(40, toJEIDirection(), progressData.getEmptyTexture(), progressData.getFillTexture());
  }

  @Override
  public Identifier texture() {
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
  public List<Fuel> ingredients() {
    return Collections.singletonList(new Fuel(requirement.requirement().required));
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, Fuel ingredient) {
    progress.draw(GuiGraphicsExtractor, 0, 0);
  }

  @Override
  public @NonNull List<Component> getTooltip(Fuel ingredient, Item.TooltipContext context, @Nullable Player player,
                                             TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, context, player, tooltipFlag);
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
        .setCustomRenderer(CustomIngredientTypes.FUEL, this)
        .addIngredients(CustomIngredientTypes.FUEL, ingredients());
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
