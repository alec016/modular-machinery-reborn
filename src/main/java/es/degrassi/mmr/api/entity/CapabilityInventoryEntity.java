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
        2,
        Integer.MAX_VALUE,
        getMode().isInput() ? Integer.MAX_VALUE : 0,
        getMode().isOutput() ? Integer.MAX_VALUE : 0,
        Direction.values()
    );
    handler.setInputSlotCount(1);
    handler.setFilter(resource -> resource
        .test(stack -> stack.getCapability(getCapability(), ItemAccess.forStack(stack)) != null)
    );
    return handler;
  }

  default void addDrops(List<ItemStack> drops) {
    getCapabilityInventory().forEach(stack -> drops.addAll(split(stack)));
  }

  IOType getMode();

  ItemCapability<T, ItemAccess> getCapability();

  ItemHandler getCapabilityInventory();

  void tickInventory();

  boolean shouldTickInventory();
}
