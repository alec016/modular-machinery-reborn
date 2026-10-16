package es.degrassi.mmr.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.PositionedSizedRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementDurabilityPerTick;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.integration.jei.category.drawable.DrawableWrappedText;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.DurabilityComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class JeiDurabilityPerTickComponent extends JeiComponent<ItemStack, RecipeRequirement<DurabilityComponent,
    RequirementDurabilityPerTick, ItemHandler>> {
  private final List<ItemStack> items;
  public JeiDurabilityPerTickComponent(RecipeRequirement<DurabilityComponent, RequirementDurabilityPerTick, ItemHandler> requirement) {
    super(requirement, 36, 0);
    items =
        requirement.requirement().getIngredient().display().resolveForStacks(ContextMap.EMPTY)
        .stream()
        .map(stack -> generateWithDurability(stack, requirement.requirement().getAmount()))
        .flatMap(List::stream)
        .unordered()
        .toList();
  }

  @Override
  public int getWidth() {
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  @Override
  public List<ItemStack> ingredients() {
    return items;
  }

  private List<ItemStack> generateWithDurability(ItemStack stack, int amount) {
    if (!stack.isDamageableItem()) throw new IllegalArgumentException("Invalid not damageable item in durability requirement");
    int maxDamage = stack.getMaxDamage();
    List<ItemStack> damagedItems = Lists.newArrayList();
    if (maxDamage <= 10) {
      for (int i = 0; i <= maxDamage; i++) {
        ItemStack copy = stack.copyWithCount(amount);
        copy.setDamageValue(i);
        damagedItems.add(copy);
      }
    } else {
      for (int i = 0; i <= 10; i++) {
        ItemStack copy = stack.copyWithCount(amount);
        copy.setDamageValue(Mth.randomBetweenInclusive(RandomSource.create(), 0, maxDamage));
        damagedItems.add(copy);
      }
    }
    return damagedItems.stream().unordered().toList();
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
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
                .transform(DrawableWrappedText.Operation.MULTIPLY, DrawableWrappedText.State.TRANSLATEY, -1)
                .transform(DrawableWrappedText.Operation.REMOVE, DrawableWrappedText.State.TRANSLATEY, 2)
        )
    );

    builder.addSlot(role(), getPosition().x(), getPosition().y())
        .addItemStacks(ingredients())
        .addRichTooltipCallback((view, tooltip) -> {
          if(this.requirement.requirement().getMode().isInput())
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.durability.consume", this.requirement.requirement().getAmount()));
          else if(this.requirement.requirement().getMode() == IOType.OUTPUT)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.durability.repair", this.requirement.requirement().getAmount()));
          tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.perTick"));
          if (requirement.chance() > 0 && requirement.chance() < 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.requirement().getMode().name().toLowerCase(Locale.ROOT), chance, "%"));
          else if (requirement.chance() == 0)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
        })
        .setStandardSlotBackground();
  }
}
