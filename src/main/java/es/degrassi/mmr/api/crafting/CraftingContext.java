package es.degrassi.mmr.api.crafting;

import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.function.Supplier;

public class CraftingContext implements ICraftingContext {

  private final MachineControllerEntity tile;
  private final RecipeHolder<MachineRecipe> recipe;
  private final Supplier<Float> progressTimeGetter;
  private final int core;
  private float baseSpeed = 1.0f;

  public CraftingContext(MachineControllerEntity tile, RecipeHolder<MachineRecipe> recipe, Supplier<Float> progressTimeGetter, int core) {
    this.tile = tile;
    this.recipe = recipe;
    this.progressTimeGetter = progressTimeGetter;
    this.core = core;
  }

  @Override
  public MachineControllerEntity getMachineTile() {
    return this.tile;
  }

  @Override
  public int getCurrentCore() {
    return this.core;
  }

  @Override
  public MachineRecipe getRecipe() {
    return this.recipe.value();
  }

  @Override
  public Identifier getRecipeId() {
    return this.recipe.id().identifier();
  }

  @Override
  public float getRemainingTime() {
    if(getRecipe() == null)
      return 0;
    return getRecipe().getRecipeTotalTickTime() - this.progressTimeGetter.get();
  }

  @Override
  public float getBaseSpeed() {
    return this.baseSpeed;
  }

  @Override
  public void setBaseSpeed(float baseSpeed) {
    this.baseSpeed = baseSpeed;
  }

  @Override
  public float getModifiedSpeed() {
    if(getRecipe() == null)
      return this.baseSpeed;
    int baseTime = getRecipe().getRecipeTotalTickTime();
    float modifiedTime = getModifiedValue(baseTime, RequirementTypeRegistration.SPEED.get(), IOType.INPUT);
    float speed = baseTime * this.baseSpeed / modifiedTime;
    return Math.max(0.01f, speed);
  }

  @Override
  public <C extends MachineComponent<T>, T> long getIntegerModifiedValue(float value, IRequirement<C, T> requirement) {
    return Math.round(getModifiedValue(value, requirement));
  }

  @Override
  public <C extends MachineComponent<T>, T> long getPerTickIntegerModifiedValue(float value, IRequirement<C, T> requirement) {
    return Math.round(getPerTickModifiedValue(value, requirement));
  }

  @Override
  public <R extends IRequirement<C, T>, C extends MachineComponent<T>, T> List<RecipeModifier<R, C, T>> getModifiers(RequirementType<R, C, T> target) {
    return tile.getComponentManager().getModifiers(target);
  }

  @Override
  public <C extends MachineComponent<T>, T> float getModifiedValue(float value, IRequirement<C, T> requirement) {
    return getModifiedValue(value, requirement.getType(), requirement.getMode());
  }

  @Override
  public <C extends MachineComponent<T>, T> float getPerTickModifiedValue(float value, IRequirement<C, T> requirement) {
    if(this.getRemainingTime() > 0)
      return getModifiedValue(value, requirement) * Math.min(this.getModifiedSpeed(), this.getRemainingTime());
    return getModifiedValue(value, requirement) * this.getModifiedSpeed();
  }

  private <R extends IRequirement<C, T>, C extends MachineComponent<T>, T> float getModifiedValue(float value, RequirementType<R, C, T> type, IOType mode) {
    float modified = value;
    for (var modifier : getModifiers(type)) {
      if (modifier.shouldApply(type, mode)) {
        modified = modifier.apply(modified);
      }
    }
    return modified;
  }

  public static class Mutable extends CraftingContext {

    private MachineRecipe recipe;
    private Identifier recipeId;

    public Mutable(MachineControllerEntity tile, int core) {
      super(tile, null, () -> 0.0f, core);
    }

    public Mutable setRecipe(MachineRecipe recipe, Identifier recipeId) {
      this.recipe = recipe;
      this.recipeId = recipeId;
      return this;
    }

    @Override
    public MachineRecipe getRecipe() {
      return this.recipe;
    }

    @Override
    public Identifier getRecipeId() {
      return this.recipeId;
    }
  }
}
