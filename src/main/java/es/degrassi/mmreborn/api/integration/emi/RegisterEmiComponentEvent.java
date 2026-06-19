package es.degrassi.mmreborn.api.integration.emi;

import es.degrassi.mmreborn.api.MMREvent;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.MachineComponent;

public class RegisterEmiComponentEvent extends MMREvent<RequirementType<?, ?, ?>, EmiComponentFactory<?,?,?,?,?>> {

  public <
      R extends RecipeRequirement<C, T, X>,
      T extends IRequirement<C, X>,
      C extends MachineComponent<X>,
      X,
      Y
  > void register(RequirementType<T, C, X> requirement, EmiComponentFactory<R, T, C, X, Y> component) {
    super.register(requirement, component);
  }
}
