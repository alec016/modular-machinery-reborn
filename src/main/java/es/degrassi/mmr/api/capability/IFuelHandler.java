package es.degrassi.mmr.api.capability;

import es.degrassi.experiencelib.api.capability.IContentsListener;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public interface IFuelHandler extends ValueIOSerializable {
  String FUEL_TAG = "fuel";
  String MAX_FUEL_TAG = "maxFuel";

  long getFuel();
  long getMaxFuel();
  void addFuel(long fuel);
  void removeFuel(long amount);

  void setFuel(long fuel);
  void setMaxFuel(long maxFuel);

  boolean burn(long amount);
  void tryBurnItem(FuelValues fuelValues);

  boolean hasSpace(long amount);

  @Override
  default void serialize(ValueOutput output) {
    output.putLong(FUEL_TAG, getFuel());
    output.putLong(MAX_FUEL_TAG, getMaxFuel());
  }

  @Override
  default void deserialize(ValueInput input) {
    setFuel(input.getLongOr(FUEL_TAG, 0L));
    setMaxFuel(input.getLongOr(MAX_FUEL_TAG, 0L));
  }

  ItemHandler getInventory();

  void setChanged();
  void setListener(IContentsListener listener);
}
