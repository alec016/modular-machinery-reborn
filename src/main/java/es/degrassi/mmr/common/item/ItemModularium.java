package es.degrassi.mmr.common.item;

import es.degrassi.mmr.common.data.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemModularium extends Item implements ItemDynamicColor {

  public ItemModularium() {
    super(new Properties());
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    if(stack.isEmpty()) {
      return 0;
    }
    return Config.machineColor;
  }
}
