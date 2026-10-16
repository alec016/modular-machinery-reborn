package es.degrassi.mmr.api.entity;

import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface DataComponentInventoryEntity<T> extends ItemDroppeable {
  default ItemHandler createDataComponentInventory() {
    return new ItemHandler(
        getMode().isInput() ? new int[]{ 0 } : new int[]{},
        getMode().isOutput() ? new int[]{ 0 } : new int[]{},
        stack -> stack.get(getDataComponent()) != null,
        64,
        Direction.values()
    );
  }

  default void addDrops(List<ItemStack> drops) {
    getDataComponentInventory().getAllStacks().forEach(stack -> drops.add(stack.copy()));
  }

  IOType getMode();

  DataComponentType<? extends T> getDataComponent();

  ItemHandler getDataComponentInventory();

  void tickInventory();

  boolean shouldTick();
}
