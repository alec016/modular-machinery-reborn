package es.degrassi.mmreborn.api.crafting;

import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessorCore;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ICraftingContext {

  /**
   * @return The {@link MachineControllerEntity} currently processing the recipe.
   */
  MachineControllerEntity getMachineTile();

  /**
   * @return The int ID of the {@link MachineProcessorCore} processing the recipe.
   */
  int getCurrentCore();

  /**
   * @return The {@link MachineRecipe} currently processed by the machine.
   */
  MachineRecipe getRecipe();

  /**
   * @return The id of the {@link MachineRecipe} currently processed by the machine.
   */
  ResourceLocation getRecipeId();

  /**
   * This time is usually in ticks, but may vary depending on what is returned by {@link ICraftingContext#getModifiedSpeed} return.
   * @return The remaining time before the end of the crafting process.
   */
  float getRemainingTime();

  /**
   * @return The base speed (in ticks) of for the processing of the current recipe. By default, '1.0' unless another
   * base speed is set using {@link ICraftingContext#setBaseSpeed(float)}.
   * This value does not take in account the upgrades that might be applied to the machine, use {@link ICraftingContext#getModifiedSpeed()} if you need the final speed value.
   */
  float getBaseSpeed();

  /**
   * Allows to set the base speed of the recipe crafting process. Upgrades will be applied on top of this value.
   * @param baseSpeed The new base speed (how much the recipe crafting process progress each tick)
   */
  void setBaseSpeed(float baseSpeed);

  /**
   * By default, the recipe processing speed is 1 per tick, but can be speeded up or slowed down if the machine have some upgrades modifiers.
   * @return The speed of the crafting process.
   */
  float getModifiedSpeed();


  /**
   * Used to apply all currently active machine upgrades to an {@link IRequirement} value.
   * @param value The value to modify (example an amount of item, energy etc...).
   * @param requirement The requirement the value depends, because machine upgrades can target a specific {@link RequirementType}.
   * @return The modified value, or the same value if no upgrades could be applied.
   */
  <C extends MachineComponent<T>, T> float getModifiedValue(float value, IRequirement<C, T> requirement);

  /**
   * Same as the method above but round the value to a {@link Long}
   */
  <C extends MachineComponent<T>, T> long getIntegerModifiedValue(float value, IRequirement<C, T> requirement);

  /**
   * Use this method only for requirements that will be executed every tick of the crafting process.
   * @param value The value to modify (example an amount of item, energy etc...).
   * @param requirement The requirement the value depends, because machine upgrades can target a specific {@link RequirementType}.
   * @return The modified value, or the same value if no upgrades could be applied.
   */
  <C extends MachineComponent<T>, T> float getPerTickModifiedValue(float value, IRequirement<C, T> requirement);

  /**
   * Same as the method above but round the value to a {@link Long}
   */
  <C extends MachineComponent<T>, T> long getPerTickIntegerModifiedValue(float value, IRequirement<C, T> requirement);

  <R extends IRequirement<C, T>, C extends MachineComponent<T>, T> List<RecipeModifier<R, C, T>> getModifiers(RequirementType<R, C, T> target);
}
