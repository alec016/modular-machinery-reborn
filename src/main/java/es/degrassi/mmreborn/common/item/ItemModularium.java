package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.data.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemModularium extends Item implements ItemDynamicColor {

  public ItemModularium() {
    super(new Properties());
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    if(stack.isEmpty()) {
      return -1;
    }
    return Config.machineColor;
  }
}
