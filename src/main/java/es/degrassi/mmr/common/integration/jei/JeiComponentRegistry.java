package es.degrassi.mmr.common.integration.jei;

import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.api.integration.jei.JeiComponentFactory;
import es.degrassi.mmr.api.integration.jei.RegisterJeiComponentEvent;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiComponent;
import es.degrassi.mmr.common.machine.MachineComponent;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class JeiComponentRegistry {
  private JeiComponentRegistry() {}
  private static Map<RequirementType<?, ?, ?>, JeiComponentFactory<?, ?>> components;

  public static void init() {
    RegisterJeiComponentEvent event = new RegisterJeiComponentEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    components = event.getMap();
  }

  public static boolean hasJeiComponent(RequirementType<?, ?, ?> requirement) {
    return components.containsKey(requirement);
  }

  @SuppressWarnings("unchecked")
  public static <
      R extends RecipeRequirement<C, T, X>,
      C extends MachineComponent<X>,
      T extends IRequirement<C, X>,
      X
  > JeiComponentFactory<R, X> getJeiComponent(RequirementType<T, C, X> requirement) {
    return (JeiComponentFactory<R, X>) components.get(requirement);
  }

  @SuppressWarnings("unchecked")
  public static <
      R extends RecipeRequirement<C, T, X>,
      C extends MachineComponent<X>,
      T extends IRequirement<C, X>,
      X
      > JeiComponent<X, R> create(R requirement) {
    return ((JeiComponentFactory<R, X>) components.get(requirement.getType())).create(requirement);
  }
}
