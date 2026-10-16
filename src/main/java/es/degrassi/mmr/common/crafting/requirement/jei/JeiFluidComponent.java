package es.degrassi.mmr.common.crafting.requirement.jei;

import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.PositionedSizedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.integration.jei.category.drawable.DrawableWrappedText;
import es.degrassi.mmr.common.machine.component.FluidComponent;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class JeiFluidComponent extends JeiComponent<FluidStack, RecipeRequirement<FluidComponent, RequirementFluid,
    FluidHandler>> {
  public JeiFluidComponent(RecipeRequirement<FluidComponent, RequirementFluid, FluidHandler> requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 16;
  }

  @Override
  public int getHeight() {
    return 16;
  }

  @Override
  public List<FluidStack> ingredients() {
    return requirement.requirement()
        .getIngredient()
        .ingredient()
        .fluids()
        .stream()
        .map(fluid -> new FluidStack(fluid, requirement.requirement().getIngredient().amount()))
        .toList();
  }

  @Override
  public @NonNull List<Component> getTooltip(FluidStack ingredient, Item.TooltipContext context, @Nullable Player player, TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, context, player, tooltipFlag);
    String mode = requirement.requirement().getMode().isInput() ? "input" : "output";
    tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.fluid." + mode, ingredient.getHoverName(), ingredient.getAmount()));

    if (requirement.chance() < 1F && requirement.chance() >= 0F) {
      String keyNever = requirement.requirement().getMode().isInput() ? "tooltip.machinery.chance.in.never" : "tooltip" +
          ".machinery.chance.out.never";
      String keyChance = requirement.requirement().getMode().isInput() ? "tooltip.machinery.chance.in" : "tooltip.machinery.chance.out";

      String chanceStr = String.valueOf(Mth.floor(requirement.chance() * 100F));
      if (requirement.chance() == 0F) {
        tooltip.add(Component.translatable(keyNever));
      } else {
        if (requirement.chance() < 0.01F) {
          chanceStr = "< 1";
        }
        chanceStr += "%";
        tooltip.add(Component.translatable(keyChance, chanceStr));
      }
    }
    return tooltip;
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    int fluid = (int) (System.currentTimeMillis() / 1000 % ingredients().size());
    Component component = Component.empty();
    String chance = Utils.decimalFormat(requirement.chance() * 100);
    if (requirement.chance() > 0 && requirement.chance() < 1)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance", chance, "%").withColor(Config.chanceColor);
    else if (requirement.chance() == 0)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance.nc").withColor(Config.chanceColor);
    Font font = Minecraft.getInstance().font;
    recipe.chanceTexts.add(
        Pair.of(
            new PositionedSizedRequirement(
                getPosition().x(),
                getPosition().y(),
                getWidth(),
                font.wordWrapHeight(component, getWidth())
            ),
            new DrawableWrappedText(List.of(component), getWidth() + 2, true)
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEX, getPosition().x())
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEY, getPosition().y())
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.SCALE, 0.75)
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEZ, 500)
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEX, (double) (getWidth() - 16) / 2)
                .transform(DrawableWrappedText.Operation.ADD, DrawableWrappedText.State.TRANSLATEX, 17)
                .transform(DrawableWrappedText.Operation.REMOVE, DrawableWrappedText.State.TRANSLATEX, Math.min(14, font.width(component)))
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEY, (double) (getHeight() - 16) / 2)
                .transform(DrawableWrappedText.Operation.REMOVE, DrawableWrappedText.State.TRANSLATEY, 1)
                .transform(DrawableWrappedText.Operation.ADD, DrawableWrappedText.State.TRANSLATEY, (double) font.lineHeight * 3/2)
        )
    );
    builder
        .addSlot(role(), getPosition().x(), getPosition().y())
        .setOverlay(
            MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(texture(), getUOffset(), getVOffset(), getWidth() + 2, getHeight() + 2),
            -1,
            -1
        )
        .setFluidRenderer(getRequirement().requirement().getIngredient().amount(), false, getWidth(), getHeight())
        .add(ingredients().get(fluid).getFluid(), getRequirement().requirement().getIngredient().amount())
        .addRichTooltipCallback((view, tooltip) -> {
          if (requirement.chance() > 0 && requirement.chance() < 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.requirement().getMode().name().toLowerCase(Locale.ROOT), chance, "%"));
          else if (requirement.chance() == 0)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
          else if (requirement.chance() == 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item." + requirement.requirement().getMode().name().toLowerCase(Locale.ROOT)));
        });
  }
}
