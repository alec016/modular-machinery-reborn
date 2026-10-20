package es.degrassi.mmr.common.item;

import net.minecraft.world.item.Item;

public abstract class AbstractColorableItem extends Item implements ItemDynamicColor {
  public AbstractColorableItem(Properties properties) {
    super(properties);
  }
}
