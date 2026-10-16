package es.degrassi.mmr.api.capability;

import es.degrassi.experiencelib.api.capability.IContentsListener;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.manager.handler.slot.ItemSlot;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
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
    this.inventory
        .getInventory()
        .stream()
        .filter(ItemSlot::isInput)
        .filter(slot -> !slot.getItemStack().isEmpty())
        .filter(slot -> slot.getItemStack().getBurnTime(RecipeType.SMELTING, fuelValues) > 0)
        .findFirst()
        .ifPresent(slot -> {
          long fuel = slot.getItemStack().getBurnTime(RecipeType.SMELTING, fuelValues);
          if (!hasSpace(fuel)) return;
          addFuel(fuel);
          ItemStack stack = slot.getItemStack();
          var remainder = stack.get(DataComponents.USE_REMAINDER);
          if (remainder != null) {
            slot.setItemStack(remainder.convertInto().create());
          } else {
            slot.extractItemBypassLimit(1, false);
          }
          slot.setChanged();
        });
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
