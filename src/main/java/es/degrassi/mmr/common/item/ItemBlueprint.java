package es.degrassi.mmr.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemBlueprint extends Item {
  public ItemBlueprint() {
    super(
      new Properties()
        .stacksTo(1)
    );
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    tooltipComponents.accept(Component.translatable("tooltip.blueprint"));
  }
}
