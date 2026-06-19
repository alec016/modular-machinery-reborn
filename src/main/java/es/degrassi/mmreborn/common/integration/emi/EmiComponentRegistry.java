package es.degrassi.mmreborn.common.integration.emi;

import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.api.integration.emi.EmiComponentFactory;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiComponentEvent;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiComponent;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class EmiComponentRegistry {
  private EmiComponentRegistry() {}
  private static Map<RequirementType<?, ?, ?>, EmiComponentFactory<?, ?, ?, ?, ?>> components;

  public static void init() {
    RegisterEmiComponentEvent event = new RegisterEmiComponentEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    components = event.getMap();
  }

  public static boolean hasEmiComponent(RequirementType<?, ?, ?> type) {
    return components.containsKey(type);
  }

  @SuppressWarnings("unchecked")
  public static <
      R extends RecipeRequirement<C, T, X>,
      C extends MachineComponent<X>,
      T extends IRequirement<C, X>,
      X,
      Y
    > EmiComponentFactory<R, T, C, X, Y> getEmiComponent(RequirementType<T, C, X> type) {
    return (EmiComponentFactory<R, T, C, X, Y>) components.get(type);
  }

  @SuppressWarnings("unchecked")
  public static <
      R extends RecipeRequirement<C, T, X>,
      C extends MachineComponent<X>,
      T extends IRequirement<C, X>,
      X,
      Y
      > EmiComponent<Y, R> create(R type) {
    return ((EmiComponentFactory<R, T, C, X, Y>) components.get(type.getType())).create(type);
  }
}
