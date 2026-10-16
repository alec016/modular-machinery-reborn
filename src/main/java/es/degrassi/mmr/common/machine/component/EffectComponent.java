package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.api.capability.EffectHandler;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EffectComponent extends MachineComponent<EffectHandler> {
  private final EffectHandler handler;
  public EffectComponent(EffectHandler handler) {
    super(IOType.NONE);
    this.handler = handler;
  }

  @Override
  public ComponentType<EffectHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_EFFECT.get();
  }

  @Override
  public @Nullable EffectHandler getContainerProvider() {
    return handler;
  }

  @Override
  public <C extends MachineComponent<EffectHandler>> C merge(C c) {
    return (C) this;
  }
}
