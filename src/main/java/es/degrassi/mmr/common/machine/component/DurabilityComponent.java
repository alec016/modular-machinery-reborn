package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueOutput;

public class DurabilityComponent extends MachineComponent<ItemHandler> {
  private final ItemHandler handler;

  public DurabilityComponent(ItemHandler handler) {
    super(IOType.INPUT);
    this.handler = handler;
  }

  @Override
  public ComponentType<ItemHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_DURABILITY.get();
  }

  @Override
  public ItemHandler getContainerProvider() {
    return handler;
  }

  @Override
  public void asTag(ValueOutput output) {
    super.asTag(output);
    output.putChild("handler", handler);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<ItemHandler>> C merge(C c) {
    DurabilityComponent comp = (DurabilityComponent) c;
    return (C) new DurabilityComponent(ItemHandler.mergeBuild(handler, comp.handler));
  }
}
