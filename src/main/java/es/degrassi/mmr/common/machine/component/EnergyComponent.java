package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergyComponent extends MachineComponent<EnergyHandler> {
  private final EnergyHandler handler;

  public EnergyComponent(EnergyHandler handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType<EnergyHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public EnergyHandler getContainerProvider() {
    return handler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<EnergyHandler>> C merge(C c) {
    EnergyComponent comp = (EnergyComponent) c;
    return (C) new EnergyComponent(
        new EnergyHandler() {
          @Override
          public long getAmountAsLong() {
            return handler.getAmountAsLong() + comp.handler.getAmountAsLong();
          }

          @Override
          public long getCapacityAsLong() {
            return handler.getCapacityAsLong() + comp.handler.getCapacityAsLong();
          }


          @Override
          public int insert(int toReceive, TransactionContext simulate) {
            int received1 = handler.insert(toReceive, simulate);
            if (received1 == toReceive) return toReceive;
            int received2 = comp.handler.insert(toReceive - received1, simulate);
            return received1 + received2;
          }

          @Override
          public int extract(int toExtract, TransactionContext simulate) {
            int extracted1 = handler.extract(toExtract, simulate);
            if (extracted1 == toExtract) return toExtract;
            int extracted2 = comp.handler.extract(toExtract - extracted1, simulate);
            return extracted1 + extracted2;
          }
        },
        getIOType()
    );
  }
}
