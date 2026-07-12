package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.screen.RecipeScreen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface RecipeHolder extends StackHolder {
  @Nullable EmiRecipe getRecipe();
  void recipeContext(EmiRecipe recipe);

  default boolean canResolve() {
    EmiRecipe recipe = getRecipe();
    return recipe != null && recipe.supportsRecipeTree() && RecipeScreen.resolve != null;
  }

  default boolean slotInteraction(Function<EmiBind, Boolean> function) {
    EmiRecipe recipe = getRecipe();
    if (canResolve()) {
      if (function.apply(EmiConfig.defaultStack)) {
        if (BoM.isDefaultRecipe(getStack(), recipe)) {
          BoM.removeRecipe(getStack(), recipe);
        } else {
          BoM.addRecipe(RecipeScreen.resolve, recipe);
        }
        EmiHistory.pop();
        return true;
      } else if (function.apply(EmiConfig.viewRecipes)) {
        BoM.addResolution(RecipeScreen.resolve, recipe);
        EmiHistory.pop();
        return true;
      }
    } else if (recipe != null && recipe.supportsRecipeTree() && function.apply(EmiConfig.defaultStack)) {
      if (BoM.isDefaultRecipe(getStack(), recipe)) {
        BoM.removeRecipe(getStack(), recipe);
      } else {
        BoM.addRecipe(getStack(), recipe);
      }
      return true;
    }
    return false;
  }
}
