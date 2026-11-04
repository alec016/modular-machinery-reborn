package es.degrassi.mmr.api.entity;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface ItemDroppeable {
  void addDrops(List<ItemStack> drops);

  default List<ItemStack> split(ItemStack stack) {
    var amount = stack.count();
    var stackSize = stack.getMaxStackSize();
    List<ItemStack> stacks = new ArrayList<>(amount / stackSize + 1);
    while (amount > 0) {
      var toExtract = Math.min(stackSize, amount);
      stacks.add(stack.copyWithCount(toExtract));
      amount -= toExtract;
    }
    return stacks;
  }
}
