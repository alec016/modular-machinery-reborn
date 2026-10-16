package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EmptyComponent extends MachineComponent<Void> {
  protected EmptyComponent() {
    super(IOType.INPUT);
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_EMPTY.get();
  }

  @Override
  public @Nullable Void getContainerProvider() {
    return null;
  }

  @Override
  public <C extends MachineComponent<Void>> C merge(C c) {
    return (C) this;
  }
}
