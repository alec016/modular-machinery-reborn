package es.degrassi.mmr.common.item;

import es.degrassi.mmr.common.data.Config;
import net.minecraft.world.item.ItemStack;

public interface ItemDynamicColor {
  default int getColorFromItemStack(ItemStack stack) {
    return Config.machineColor;
  }
}
