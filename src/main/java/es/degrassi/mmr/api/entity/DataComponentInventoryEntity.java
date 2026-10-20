package es.degrassi.mmr.api.entity;

import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface DataComponentInventoryEntity<T> extends ItemDroppeable {
  default ItemHandler createDataComponentInventory() {
    ItemHandler handler = new ItemHandler(1, 64,
        getMode().isInput() ? 64 : 0,
        getMode().isOutput() ? 64 : 0,
        Direction.values()
    );
    handler.setFilter(resource -> resource.toStack().get(getDataComponent()) != null);
    return handler;
  }

  default void addDrops(List<ItemStack> drops) {
    getDataComponentInventory().forEach(stack -> drops.add(stack.copy()));
  }

  IOType getMode();

  DataComponentType<? extends T> getDataComponent();

  ItemHandler getDataComponentInventory();

  void tickInventory();

  boolean shouldTick();
}
