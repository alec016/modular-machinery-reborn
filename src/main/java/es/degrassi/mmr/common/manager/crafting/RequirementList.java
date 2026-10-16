package es.degrassi.mmr.common.manager.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.manager.ComponentManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequirementList<R extends IRequirement<C, T>, C extends MachineComponent<T>, T> implements IRequirementList<C> {

  @Getter
  private final Map<Double, List<RequirementWithFunction<R, C, T>>> processRequirements = Maps.newHashMap();
  @Getter
  private final List<RequirementWithFunction<R, C, T>> tickableRequirements = Lists.newArrayList();
  @Getter
  private final List<RequirementWithFunction<R, C, T>> worldConditions = Lists.newArrayList();
  @Getter
  private final List<RequirementWithFunction<R, C, T>> inventoryConditions = Lists.newArrayList();
  private RecipeRequirement<C, R, T> currentRequirement;

  @SuppressWarnings("unchecked")
  public void setCurrentRequirement(RecipeRequirement<?, ?, ?> requirement) {
    this.currentRequirement = (RecipeRequirement<C, R, T>) requirement;
  }

  @Override
  public void processOnStart(RequirementFunction<C> function) {
    this.processRequirements.computeIfAbsent(0.0D, delay -> new ArrayList<>()).add(new RequirementWithFunction<>(this.currentRequirement, function));
  }

  @Override
  public void processOnEnd(RequirementFunction<C> function) {
    this.processRequirements.computeIfAbsent(1.0D, delay -> new ArrayList<>()).add(new RequirementWithFunction<>(this.currentRequirement, function));
  }

  @Override
  public void processEachTick(RequirementFunction<C> function) {
    this.tickableRequirements.add(new RequirementWithFunction<>(this.currentRequirement, function));
  }

  @Override
  public void worldCondition(RequirementFunction<C> function) {
    this.worldConditions.add(new RequirementWithFunction<>(this.currentRequirement, function));
  }

  @Override
  public void inventoryCondition(RequirementFunction<C> function) {
    this.inventoryConditions.add(new RequirementWithFunction<>(this.currentRequirement, function));
  }

  @Override
  public void processDelayed(double baseDelay, RequirementFunction<C> function) {
    this.processRequirements.computeIfAbsent(baseDelay, delay -> new ArrayList<>()).add(new RequirementWithFunction<>(this.currentRequirement, function));
  }

  @Override
  public void process(IOType mode, RequirementFunction<C> function) {
    this.processDelayed(mode.isInput() ? 0.0D : 1.0D, function);
  }

  public record RequirementWithFunction<
      R extends IRequirement<C, T>,
      C extends MachineComponent<T>,
      T
      >(RecipeRequirement<C, R, T> requirement, RequirementFunction<C> function) {
    public CraftingResult process(ComponentManager manager, ICraftingContext context) {
      C component = requirement.findComponent(manager, context);
      if (component == null)
        return CraftingResult.error(requirement.requirement().getMissingComponentErrorMessage(requirement.requirement().getMode()));
      return this.function.process(component, context);
    }
  }
}
