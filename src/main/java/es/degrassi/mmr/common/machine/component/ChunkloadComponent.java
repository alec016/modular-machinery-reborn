package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.util.Chunkloader;

public class ChunkloadComponent extends MachineComponent<Chunkloader> {
  private final ChunkloaderEntity entity;

  public ChunkloadComponent(ChunkloaderEntity entity) {
    super(IOType.OUTPUT);
    this.entity = entity;
  }

  @Override
  public ComponentType<Chunkloader> getComponentType() {
    return ComponentRegistration.COMPONENT_CHUNKLOAD.get();
  }

  @Override
  public Chunkloader getContainerProvider() {
    return entity.getChunkloader();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<Chunkloader>> C merge(C c) {
    return (C) this;
  }
}
