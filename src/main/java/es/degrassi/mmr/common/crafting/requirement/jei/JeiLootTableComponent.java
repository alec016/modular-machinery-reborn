package es.degrassi.mmr.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementLootTable;
import es.degrassi.mmr.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmr.common.machine.component.ItemComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.util.LootTableHelper;
import es.degrassi.mmr.common.util.LootTableHelper.LootData;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class JeiLootTableComponent extends JeiComponent<Identifier, RecipeRequirement<ItemComponent,
    RequirementLootTable, ItemHandler>> {
  public JeiLootTableComponent(RecipeRequirement<ItemComponent, RequirementLootTable, ItemHandler> requirement) {
    super(requirement, 36, 0);
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
  public List<Identifier> ingredients() {
    return Collections.singletonList(requirement.requirement().getLootTable());
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    List<LootData> loots = LootTableHelper.getLootsForTable(requirement.requirement().getLootTable());
    List<ItemStack> ingredients = Lists.newArrayList(loots.stream().map(LootData::stack).toList());
    builder.addSlot(role(), getPosition().x(), getPosition().y())
        .addItemStacks(ingredients)
        .addRichTooltipCallback((view, tooltips) -> {
          LootData data = view.getDisplayedIngredient()
              .flatMap(ingredient -> loots.stream().filter(lootData -> ItemStack.isSameItemSameComponents(lootData.stack(), ingredient.getItemStack().get())).findFirst())
              .orElse(null);
          if (data == null)
            return;
          if (data.chance() != 1) {
            double percentage = data.chance() * 100;
            if (percentage < 0.01F)
              tooltips.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.requirement().getMode().name().toLowerCase(Locale.ROOT), "<0.01", "%"));
            else {
              BigDecimal decimal = BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
              if (decimal.scale() <= 0 || decimal.signum() == 0 || decimal.stripTrailingZeros().scale() <= 0)
                tooltips.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.requirement().getMode().name().toLowerCase(Locale.ROOT), decimal.intValue(), "%"));
              else
                tooltips.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.requirement().getMode().name().toLowerCase(Locale.ROOT), decimal.doubleValue(), "%"));
            }
            if (!data.rolls().isEmpty())
              tooltips.add(Component.literal(data.rolls()));
            if (!data.bonusRolls().isEmpty())
              tooltips.add(Component.literal(data.bonusRolls()));
          }
        })
        .setStandardSlotBackground();
  }
}
