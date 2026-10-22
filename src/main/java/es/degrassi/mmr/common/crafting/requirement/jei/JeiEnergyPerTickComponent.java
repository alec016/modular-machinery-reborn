package es.degrassi.mmr.common.crafting.requirement.jei;

import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementEnergyPerTick;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Energy;
import es.degrassi.mmr.common.machine.component.EnergyComponent;
import es.degrassi.mmr.common.manager.handler.SimpleEnergyHandler;
import es.degrassi.mmr.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class JeiEnergyPerTickComponent extends JeiComponent<Energy, RecipeRequirement<EnergyComponent, RequirementEnergyPerTick, SimpleEnergyHandler>> {
  private int width = 16;
  private int height = 52;
  private int recipeTime;

  public JeiEnergyPerTickComponent(RecipeRequirement<EnergyComponent, RequirementEnergyPerTick, SimpleEnergyHandler> requirement) {
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
  public List<Energy> ingredients() {
    return Collections.singletonList(new Energy(requirement.requirement().getRequiredEnergyPerTick()));
  }

  @Override
  public void render(GuiGraphicsExtractor GuiGraphicsExtractor, Energy ingredient) {
    width += 2;
    height += 2;
    super.render(GuiGraphicsExtractor, ingredient);
    width -= 2;
    height -= 2;
  }

  @Override
  public @NonNull List<Component> getTooltip(Energy ingredient, Item.TooltipContext context, @Nullable Player player, TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, context, player, tooltipFlag);
    String mode = requirement.requirement().getMode().isInput() ? "input" : "output";
    tooltip.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.energy.total." + mode,
            Utils.format(requirement.requirement().requirementPerTick * recipeTime),
            Utils.format(requirement.requirement().requirementPerTick)
        )
    );
    return tooltip;
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    this.recipeTime = recipe.getRecipeTotalTickTime();
    builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .setCustomRenderer(CustomIngredientTypes.ENERGY, this)
        .addIngredients(CustomIngredientTypes.ENERGY, ingredients());
  }
}
