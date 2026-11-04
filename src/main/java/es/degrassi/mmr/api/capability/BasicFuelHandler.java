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
    if (this.fuel >= amount) {
      removeFuel(amount);
      return true;
    }

    return false;
  }

  @Override
  public void tryBurnItem(FuelValues fuelValues) {
    ItemStack stack = inventory.getStack(0);
    if (stack.isEmpty()) return;
    long fuel = fuelValues.burnDuration(stack);
    if (fuel <= 0 || !hasSpace(fuel)) return;

    var remainder = stack.get(DataComponents.USE_REMAINDER);
    if (remainder != null) {
      ItemStack remainderStack = remainder.convertInto().create();
      if (!inventory.canPlaceOutput(1, remainderStack)) return;
      addFuel(fuel);
      inventory.set(0, inventory.getResourceFrom(stack), stack.getCount() - 1);
      inventory.addToOutputs(remainderStack, 1);
    } else {
      addFuel(fuel);
      inventory.set(0, inventory.getResourceFrom(stack), stack.getCount() - 1);
    }
    setChanged();
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
