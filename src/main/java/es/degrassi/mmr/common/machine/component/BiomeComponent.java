package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.entity.BiomeReaderEntity;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.resources.Identifier;

import java.util.List;

public class BiomeComponent extends MachineComponent<List<Identifier>> {
  private final BiomeReaderEntity entity;

  public BiomeComponent(BiomeReaderEntity entity) {
    super(IOType.INPUT);
    this.entity = entity;
  }

  @Override
  public ComponentType<List<Identifier>> getComponentType() {
    return ComponentRegistration.COMPONENT_BIOME.get();
  }

  @Override
  public List<Identifier> getContainerProvider() {
    return List.of(entity.getLevel().getBiome(entity.getBlockPos()).unwrapKey().get().identifier());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<List<Identifier>>> C merge(C c) {
    return (C) this;
  }
}
