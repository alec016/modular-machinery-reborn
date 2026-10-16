package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.api.capability.EntityHandler;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.crafting.requirement.entity.RequirementEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;

import java.util.List;

public class EntityComponent extends MachineComponent<EntityHandler> {
  private final EntityHandler handler;
  private final List<RequirementEntity.Action> validActions;
  public EntityComponent(IOType ioType, EntityHandler handler, RequirementEntity.Action...validActions) {
    super(ioType);
    this.handler = handler;
    this.validActions = List.of(validActions);
  }

  @Override
  public ComponentType<EntityHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENTITY.get();
  }

  @Override
  public EntityHandler getContainerProvider() {
    return handler;
  }

  public boolean isValidAction(RequirementEntity.Action action) {
    return this.validActions.contains(action);
  }

  @Override
  public <C extends MachineComponent<EntityHandler>> boolean canMerge(C c) {
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<EntityHandler>> C merge(C c) {
    return (C) this;
  }
}
