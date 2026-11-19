package es.degrassi.mmreborn.common.item;

import java.util.List;

import es.degrassi.mmreborn.common.data.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemBlueprint extends Item implements ItemDynamicColor {
  public ItemBlueprint() {
    super(
      new Properties()
        .stacksTo(1)
    );
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    if(stack.isEmpty() || tintIndex != 4) {
      return -1;
    }
    return Config.machineColor;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
    pTooltipComponents.add(Component.translatable("tooltip.blueprint"));
  }
}
