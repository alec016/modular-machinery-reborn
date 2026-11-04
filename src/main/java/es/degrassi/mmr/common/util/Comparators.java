package es.degrassi.mmr.common.util;

import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.PartialBlockState;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Comparator;
import java.util.Map;

public final class Comparators {
  private Comparators() {}

  private static final Comparator<MachineRecipe> RECIPE_PRIORITY_COMPARATOR = Comparator.comparingInt(MachineRecipe::getConfiguredPriority);

  // Orders the block ingredients based on if it contains a fluid partial state or not
  // fluids goes last, so they don't break the rest of placement as the flowing fluid is not replaceable by mmr
  public static final Comparator<Map.Entry<BlockPos, BlockIngredient>> STRUCTURE_COMPARATOR = Comparator.comparing(entry -> entry
      .getValue()
      .getAll()
      .stream()
      .anyMatch(PartialBlockState::isFluid)
  );

  public static int compare(RecipeHolder<MachineRecipe> holder1, RecipeHolder<MachineRecipe> holder2) {
    return RECIPE_PRIORITY_COMPARATOR.reversed().compare(holder1.value(), holder2.value());
  }
}
