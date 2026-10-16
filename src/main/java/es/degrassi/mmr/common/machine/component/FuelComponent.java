package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.api.capability.BasicFuelHandler;
import es.degrassi.mmr.api.capability.IFuelHandler;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;

@SuppressWarnings("unchecked")
public class FuelComponent extends MachineComponent<IFuelHandler> {
  private final IFuelHandler fuelHandler;
  public FuelComponent(IFuelHandler fuelHandler) {
    super(IOType.INPUT);
    this.fuelHandler = fuelHandler;
  }

  @Override
  public ComponentType<IFuelHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_FUEL.get();
  }

  @Override
  public IFuelHandler getContainerProvider() {
    return fuelHandler;
  }

  @Override
  public <C extends MachineComponent<IFuelHandler>> C merge(C c) {
    FuelComponent comp = (FuelComponent) c;

    return (C) new FuelComponent(new BasicFuelHandler(null) {
      @Override
      public void addFuel(long fuel) {
        if (fuelHandler.getFuel() + fuel <= fuelHandler.getMaxFuel() && fuelHandler.getFuel() + fuel >= 0) {
          fuelHandler.addFuel(fuel);
          return;
        }
        var rest = Math.min(fuelHandler.getMaxFuel() - fuelHandler.getFuel(), fuel);
        fuelHandler.addFuel(rest);
        fuel -= rest;
        if (comp.fuelHandler.getFuel() + fuel <= comp.fuelHandler.getMaxFuel() && comp.fuelHandler.getFuel() + fuel >= 0) {
          comp.fuelHandler.addFuel(fuel);
          return;
        }
        rest = Math.min(comp.fuelHandler.getMaxFuel() - comp.fuelHandler.getFuel(), fuel);
        comp.fuelHandler.addFuel(rest);
      }

      @Override
      public boolean burn(long amount) {
        if (!fuelHandler.burn(amount) && !comp.fuelHandler.burn(amount)) {
          var first = fuelHandler.getFuel();
          var second = comp.fuelHandler.getFuel();
          if (first + second >= amount) {
            fuelHandler.removeFuel(first);
            amount -= first;
            comp.fuelHandler.removeFuel(amount);
            return true;
          }
          return false;
        }
        return true;
      }

      @Override
      public void removeFuel(long fuel) {
        if (fuelHandler.getFuel() - fuel >= 0) {
          fuelHandler.removeFuel(fuel);
          return;
        }
        var rest = Math.min(fuelHandler.getFuel(), fuel);
        fuelHandler.removeFuel(rest);
        fuel -= rest;
        if (comp.fuelHandler.getFuel() - fuel >= 0) {
          comp.fuelHandler.removeFuel(fuel);
          return;
        }
        rest = Math.min(comp.fuelHandler.getFuel(), fuel);
        comp.fuelHandler.removeFuel(rest);
      }

      @Override
      public boolean hasSpace(long amount) {
        if (!fuelHandler.hasSpace(amount) && !comp.fuelHandler.hasSpace(amount)) {
          var first = fuelHandler.getMaxFuel() - fuelHandler.getFuel();
          var second = comp.fuelHandler.getMaxFuel() - comp.fuelHandler.getFuel();
          return first + second >= amount;
        }
        return true;
      }

      @Override
      public long getFuel() {
        return fuelHandler.getFuel() + comp.fuelHandler.getFuel();
      }

      @Override
      public long getMaxFuel() {
        return fuelHandler.getMaxFuel() + comp.fuelHandler.getMaxFuel();
      }
    });
  }

  public boolean canStartRecipe(long amount) {
    return this.getContainerProvider().getFuel() >= amount;
  }
}
