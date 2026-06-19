package es.degrassi.mmreborn.api.integration.emi;

import es.degrassi.mmreborn.api.MMREvent;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.MachineComponent;

public class RegisterEmiRequirementToStackEvent extends MMREvent<RequirementType<?, ?, ?>, EmiStackFactory<?, ?, ?, ?>> {

  public <
      R extends RecipeRequirement<T, C, X>,
      C extends IRequirement<T, X>,
      T extends MachineComponent<X>,
      X
  > void register(RequirementType<C, T, X> requirement, EmiStackFactory<R, C, T, X> factory) {
    super.register(requirement, factory);
  }
}
