package es.degrassi.mmr.common.block;

import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import es.degrassi.mmr.common.block.base.BlockEnergyHatch;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.EnergyOutputHatchEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockEnergyOutputHatch extends BlockEnergyHatch {

  public BlockEnergyOutputHatch(EnergyHatchSize type) {
    super(type);
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
      if (EnergyDisplayUtil.displayFETooltip) {
        tooltip.accept(Component.translatable("tooltip.energyhatch.storage", type.maxEnergy).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.energyhatch.out.transfer", type.transferLimit).withStyle(ChatFormatting.GRAY));
      }
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new EnergyOutputHatchEntity(pos, state, type);
  }
}