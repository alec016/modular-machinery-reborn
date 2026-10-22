package es.degrassi.mmr.api.capability;

import es.degrassi.experiencelib.api.capability.IContentsListener;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.FuelValues;

@Getter
@Setter
public class BasicFuelHandler implements IFuelHandler {
  private long fuel;
  private long maxFuel;

  private final ItemHandler inventory;
  private IContentsListener listener;

  public BasicFuelHandler(ItemHandler inventory) {
    this.inventory = inventory;
  }

  @Override
  public void addFuel(long fuel) {
    if (this.fuel >= this.maxFuel) return;
    this.fuel += fuel;
    setChanged();
  }

  public void removeFuel(long fuel) {
    if (this.fuel < fuel) return;
    this.fuel -= fuel;
    setChanged();
  }

  @Override
  public boolean burn(long amount) {
    //If the machine have sufficient fuel, just burn it and return true
    if (this.fuel >= amount) {
      removeFuel(amount);
      return true;
    }

    //If the machine still don't have the required fuel amount return false, the fuel requirement will error
    return false;
  }

  @Override
  public void tryBurnItem(FuelValues fuelValues) {
    // The legacy per-slot ItemSlot view (es.degrassi.mmr.common.manager.handler.slot) was removed;
    // iterate the ResourceHandler<ItemResource> slots by index instead.
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getStack(i);
      if (stack.isEmpty()) continue;
      long fuel = fuelValues.burnDuration(stack);
      if (fuel <= 0 || !hasSpace(fuel)) continue;

      addFuel(fuel);
      var remainder = stack.get(DataComponents.USE_REMAINDER);
      if (remainder != null) {
        inventory.set(i, inventory.getResourceFrom(remainder.convertInto().create()), 1);
      } else {
        inventory.set(i, inventory.getResourceFrom(stack), stack.getCount() - 1);
      }
      setChanged();
      return;
    }
  }

  @Override
  public boolean hasSpace(long amount) {
    return (this.fuel + amount) <= maxFuel;
  }

  @Override
  public void setChanged() {
    if (listener != null)
      listener.onContentsChanged();
  }
}
