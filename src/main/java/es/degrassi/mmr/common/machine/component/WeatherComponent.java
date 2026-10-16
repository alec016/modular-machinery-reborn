package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.api.crafting.requirement.WeatherType;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;

import javax.annotation.Nullable;

public class WeatherComponent extends MachineComponent<WeatherType> {
  public WeatherComponent() {
    super(IOType.INPUT);
  }

  @Override
  public ComponentType<WeatherType> getComponentType() {
    return ComponentRegistration.COMPONENT_WEATHER.get();
  }

  @Override
  public @Nullable WeatherType getContainerProvider() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<WeatherType>> C merge(C c) {
    return (C) this;
  }
}
