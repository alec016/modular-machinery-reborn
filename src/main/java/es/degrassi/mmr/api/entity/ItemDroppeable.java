package es.degrassi.mmr.api.entity;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemDroppeable {
  void addDrops(List<ItemStack> drops);
}
