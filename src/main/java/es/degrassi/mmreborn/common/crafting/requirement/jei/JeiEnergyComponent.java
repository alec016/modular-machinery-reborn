package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.machine.component.EnergyComponent;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import es.degrassi.mmreborn.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class JeiEnergyComponent extends JeiComponent<Long, RecipeRequirement<EnergyComponent, RequirementEnergy, IEnergyHandler>> {
  private int width = 16;
  private int height = 52;

  public JeiEnergyComponent(RecipeRequirement<EnergyComponent, RequirementEnergy, IEnergyHandler> requirement) {
    super(requirement, 18, 0);
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
  public List<Long> ingredients() {
    return Collections.singletonList(requirement.requirement().getRequiredEnergy());
  }

  @Override
  public void render(GuiGraphics guiGraphics, Long ingredient) {
    width += 2;
    height += 2;
    super.render(guiGraphics, ingredient);
    width -= 2;
    height -= 2;
  }

  @Override
  public List<Component> getTooltip(Long ingredient, TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, tooltipFlag);
    String mode = requirement.requirement().getMode().isInput() ? "input" : "output";
    tooltip.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.energy." + mode,
            Utils.format(requirement.requirement().requirement)
        )
    );
    return tooltip;
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .setCustomRenderer(CustomIngredientTypes.LONG, this)
        .addIngredients(CustomIngredientTypes.LONG, ingredients());
  }
}
