package es.degrassi.mmr.api.integration.jei;

import es.degrassi.mmr.api.MMREvent;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.machine.MachineComponent;

public class RegisterJeiComponentEvent extends MMREvent<RequirementType<?, ?, ?>, JeiComponentFactory<?, ?>> {
  public <
      R extends RecipeRequirement<?, T, X>,
      T extends IRequirement<C, X>,
      C extends MachineComponent<X>,
      X,
      Y
  > void register(RequirementType<T, C, X> requirement, JeiComponentFactory<R, Y> component) {
    super.register(requirement, component);
  }
}
