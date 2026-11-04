package es.degrassi.mmr.api.entity;

import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface DataComponentInventoryEntity<T> extends ItemDroppeable {
  default ItemHandler createDataComponentInventory() {
    ItemHandler handler = new ItemHandler(2, 64,
        getMode().isInput() ? 64 : 0,
        getMode().isOutput() ? 64 : 0,
        Direction.values()
    );
    handler.setInputSlotCount(1);
    handler.setFilter(resource -> resource.get(getDataComponent()) != null);
    return handler;
  }

  default void addDrops(List<ItemStack> drops) {
    getDataComponentInventory().forEach(stack -> drops.addAll(split(stack)));
  }

  IOType getMode();

  DataComponentType<? extends T> getDataComponent();

  ItemHandler getDataComponentInventory();

  void tickInventory();

  boolean shouldTick();
}
