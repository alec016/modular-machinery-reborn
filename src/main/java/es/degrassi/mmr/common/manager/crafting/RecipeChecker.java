package es.degrassi.mmr.common.manager.crafting;

import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import lombok.Getter;
import net.minecraft.world.item.crafting.RecipeHolder;
import com.google.common.collect.Lists;

import java.util.List;

// TODO: maybe this in async thread?
public class RecipeChecker<T extends MachineRecipe> {

  @Getter
  private final RecipeHolder<T> recipe;
  private final List<RecipeRequirement<?, ?, ?>> inventoryRequirements;
  private final List<RecipeRequirement<?, ?, ?>> checkedInventoryRequirements;
  private final List<RecipeRequirement<?, ?, ?>> worldRequirements;
  @Getter
  private final boolean inventoryRequirementsOnly;
  @Getter
  private boolean inventoryRequirementsOk = false;

  public RecipeChecker(RecipeHolder<T> recipe) {
    this.recipe = recipe;
    this.inventoryRequirements =
        recipe.value().getRequirements().stream().filter(r -> !r.getType().isWorldRequirement()).toList();
    this.checkedInventoryRequirements = Lists.newArrayList();
    this.worldRequirements =
        recipe.value().getRequirements().stream().filter(r -> r.getType().isWorldRequirement()).toList();
    this.inventoryRequirementsOnly =
        recipe.value().getRequirements().stream().noneMatch(r -> r.getType().isWorldRequirement());
  }

  public boolean check(MachineControllerEntity tile, ICraftingContext context, boolean inventoryChanged) {
    if (inventoryChanged) {
      this.checkedInventoryRequirements.clear();
      this.inventoryRequirementsOk = false;

      for (RecipeRequirement<?, ?, ?> requirement : this.inventoryRequirements) {
        if (this.checkedInventoryRequirements.contains(requirement))
          continue;
        this.checkedInventoryRequirements.add(requirement);
        if (!checkRequirement(requirement, tile, context))
          return false;
      }

      this.inventoryRequirementsOk = true;
    }

    if (!this.inventoryRequirementsOk)
      return false;
    else
      return this.worldRequirements.stream().allMatch(r -> checkRequirement(r, tile, context));
  }

  private boolean checkRequirement(RecipeRequirement<?, ?, ?> requirement, MachineControllerEntity tile, ICraftingContext context) {
    return requirement.test(tile.getComponentManager(), context).isSuccess();
  }
}
