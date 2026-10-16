package es.degrassi.mmr.common.item;

import es.degrassi.mmr.common.data.Config;
import net.minecraft.world.item.ItemStack;

public interface ItemDynamicColor {
  default int getColorFromItemstack(ItemStack stack, int tintIndex) {
    return Config.machineColor;
  }
}
