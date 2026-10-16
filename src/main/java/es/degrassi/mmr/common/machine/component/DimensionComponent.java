package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.entity.DimensionalDetectorEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.resources.Identifier;

public class DimensionComponent extends MachineComponent<Identifier> {
  private final DimensionalDetectorEntity entity;

  public DimensionComponent(DimensionalDetectorEntity entity) {
    super(IOType.INPUT);
    this.entity = entity;
  }

  @Override
  public ComponentType<Identifier> getComponentType() {
    return ComponentRegistration.COMPONENT_DIMENSION.get();
  }

  @Override
  public Identifier getContainerProvider() {
    return entity.getLevel().dimension().identifier();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<Identifier>> C merge(C c) {
    return (C) this;
  }
}
