package es.degrassi.mmr.api.entity;


import es.degrassi.mmr.api.handler.FilterConverterRegistry;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public interface FiltereableEntity<T, E> {
  String FILTER_KEY = "custom_filter";
  T getFilter();
  void setFilter(T filter);

  BlockPos getBlockPos();

  ItemHandler getFilterInventory();

  default ItemHandler createFilterInventory() {
    ItemHandler handler = new ItemHandler(1, 1, 1, 0);
    handler.setFilter(resource -> {
      var stack = resource.toStack();
      var hasConverter = FilterConverterRegistry.hasConverter(stack);
      var value = FilterConverterRegistry.convert(stack);
      return hasConverter && value != null && value.getClass().equals(getFilter().getClass());
    });
    return handler;
  }

  void serializeFilter(ValueOutput output);

  void deserializeFilter(ValueInput input);

  @Nullable
  Component getFilterComponent();

  E toFilterRender();
}
