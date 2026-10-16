package es.degrassi.mmr.api.crafting;

import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.manager.crafting.ProcessorType;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public interface IProcessor extends ValueIOSerializable {

  ProcessorType<? extends IProcessor> getType();

  MachineControllerEntity tile();

  void tick();

  void reset();

  void setMachineInventoryChanged();

  default void setSearchImmediately() {

  }
}
