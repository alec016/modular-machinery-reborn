package es.degrassi.mmr.common.item;

import es.degrassi.mmr.common.block.base.TooltipAppendable;
import es.degrassi.mmr.common.data.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ItemBlockMachineComponent extends BlockItem implements ItemDynamicColor {
  public ItemBlockMachineComponent(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public int getColorFromItemStack(ItemStack stack) {
    if(stack.isEmpty()) {
      return 0;
    }
    return Config.machineColor;
  }

  @Override
  public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
    super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    if (getBlock() instanceof TooltipAppendable appendable) {
      appendable.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
  }
}
