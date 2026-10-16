package es.degrassi.mmr.api.entity;

import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.List;

public interface CapabilityInventoryEntity<T> extends ItemDroppeable {
  default ItemHandler createCapabilityInventory() {
    var handler = new ItemHandler(
        1,
        Integer.MAX_VALUE,
        getMode().isInput() ? Integer.MAX_VALUE : 0,
        getMode().isOutput() ? Integer.MAX_VALUE : 0,
        Direction.values()
    );
    handler.setFilter(stack -> stack.toStack().getCapability(getCapability(), ItemAccess.forStack(stack.toStack())) != null);
    return handler;
  }

  default void addDrops(List<ItemStack> drops) {
    getCapabilityInventory().forEach(stack -> drops.add(stack.copy()));
  }

  IOType getMode();

  ItemCapability<T, ItemAccess> getCapability();

  ItemHandler getCapabilityInventory();

  void tickInventory();

  boolean shouldTickInventory();
}
