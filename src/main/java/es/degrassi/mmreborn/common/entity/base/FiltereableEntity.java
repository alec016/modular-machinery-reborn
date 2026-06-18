package es.degrassi.mmreborn.common.entity.base;


import es.degrassi.mmreborn.api.handler.FilterConverterRegistry;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public interface FiltereableEntity<T, E> {
  String FILTER_KEY = "custom_filter";
  T getFilter();
  void setFilter(T filter);

  BlockPos getBlockPos();

  ItemHandler getFilterInventory();

  default ItemHandler createFilterInventory() {
    return new ItemHandler(
        new int[]{ 0 },
        new int[]{},
        stack -> {
          var hasConverter = FilterConverterRegistry.hasConverter(stack);
          var value = FilterConverterRegistry.convert(stack);
          return hasConverter && value != null && value.getClass().equals(getFilter().getClass());
        },
        1
    );
  }

  CompoundTag serializeFilter(HolderLookup.Provider provider);

  void deserializeFilter(CompoundTag tag, HolderLookup.Provider provider);

  @Nullable
  Component getFilterComponent();

  E toFilterRender();
}
