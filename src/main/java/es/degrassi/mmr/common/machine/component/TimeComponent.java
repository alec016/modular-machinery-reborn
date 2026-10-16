package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.util.IntRange;
import org.jetbrains.annotations.Nullable;

public class TimeComponent extends MachineComponent<IntRange> {

  public TimeComponent() {
    super(IOType.INPUT);
  }

  @Override
  public ComponentType<IntRange> getComponentType() {
    return ComponentRegistration.COMPONENT_TIME.get();
  }

  @Override
  public @Nullable IntRange getContainerProvider() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<IntRange>> C merge(C c) {
    return (C) this;
  }
}
