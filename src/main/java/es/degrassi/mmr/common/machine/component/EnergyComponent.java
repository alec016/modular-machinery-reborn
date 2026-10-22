package es.degrassi.mmr.common.machine.component;

import com.google.gson.JsonObject;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.manager.handler.MergedEnergyHandler;
import es.degrassi.mmr.common.manager.handler.SimpleEnergyHandler;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.world.level.storage.ValueOutput;

public class EnergyComponent extends MachineComponent<SimpleEnergyHandler> {
  private final SimpleEnergyHandler handler;

  public EnergyComponent(SimpleEnergyHandler handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType<SimpleEnergyHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public SimpleEnergyHandler getContainerProvider() {
    return handler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<SimpleEnergyHandler>> C merge(C c) {
    EnergyComponent comp = (EnergyComponent) c;
    return (C) new EnergyComponent(new MergedEnergyHandler(handler, comp.handler), getIOType());
  }

  @Override
  public JsonObject asJson() {
    var json = super.asJson();
    var energyHandler = new JsonObject();
    energyHandler.addProperty("energy", handler.getAmountAsLong());
    energyHandler.addProperty("capacity", handler.getCapacityAsLong());
    energyHandler.addProperty("maxInput", handler.getMaxInsert());
    energyHandler.addProperty("maxExtract", handler.getMaxExtract());
    return json;
  }

  @Override
  public void asTag(ValueOutput output) {
    super.asTag(output);
    output.putChild("energyHandler", getContainerProvider());
  }
}
