package es.degrassi.mmreborn.api.integration.jei;

import es.degrassi.mmreborn.api.MMREvent;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.MachineComponent;

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
