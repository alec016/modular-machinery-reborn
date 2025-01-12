package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;

public class EnergyComponent extends MachineComponent<IEnergyHandler> {
  private final IEnergyHandler handler;

  public EnergyComponent(IEnergyHandler handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

  @Override
  public IEnergyHandler getContainerProvider() {
    return handler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> C merge(C c) {
    EnergyComponent comp = (EnergyComponent) c;
    return (C) new EnergyComponent(
        new IEnergyHandler() {
          @Override
          public long getCurrentEnergy() {
            return handler.getCurrentEnergy() + comp.handler.getCurrentEnergy();
          }

          @Override
          public void setCurrentEnergy(long energy) {

          }

          @Override
          public long getMaxEnergy() {
            return handler.getMaxEnergy() + comp.handler.getMaxEnergy();
          }

          @Override
          public void setCanExtract(boolean b) {
            handler.setCanExtract(b);
            comp.handler.setCanExtract(b);
          }

          @Override
          public void setCanInsert(boolean b) {
            handler.setCanExtract(b);
            comp.handler.setCanExtract(b);
          }

          @Override
          public int receiveEnergy(int toReceive, boolean simulate) {
            int received1 = handler.receiveEnergy(toReceive, simulate);
            toReceive -= received1;
            int received2 = comp.handler.receiveEnergy(toReceive, simulate);
            return received1 + received2;
          }

          @Override
          public int extractEnergy(int toExtract, boolean simulate) {
            int extracted1 = handler.extractEnergy(toExtract, simulate);
            toExtract -= extracted1;
            int extracted2 = comp.handler.extractEnergy(toExtract, simulate);
            return extracted1 + extracted2;
          }

          @Override
          public int getEnergyStored() {
            return (int) getCurrentEnergy();
          }

          @Override
          public int getMaxEnergyStored() {
            return (int) getMaxEnergy();
          }

          @Override
          public boolean canExtract() {
            return true;
          }

          @Override
          public boolean canReceive() {
            return true;
          }
        },
        getIOType()
    );
  }
}
