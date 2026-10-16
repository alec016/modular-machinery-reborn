package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import org.jetbrains.annotations.Nullable;

public class DurationComponent extends MachineComponent<Void> {
  public DurationComponent() {
    super(IOType.INPUT);
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_DURATION.get();
  }

  @Override
  public @Nullable Void getContainerProvider() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<Void>> C merge(C c) {
    return (C) this;
  }
}
