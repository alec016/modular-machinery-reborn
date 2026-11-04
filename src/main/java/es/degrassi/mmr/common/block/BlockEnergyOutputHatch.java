package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
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

  public BlockEnergyOutputHatch(Properties id, EnergyHatchSize type) {
    super(id, type);
  }

  @Override
  protected MapCodec<BlockEnergyOutputHatch> codec() {
    return BlockCodecs.ENERGY_OUTPUT.mapCodec();
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    if (EnergyDisplayUtil.displayFETooltip) {
      tooltip.accept(Component.translatable("tooltip.energyhatch.storage", size.maxEnergy).withStyle(ChatFormatting.GRAY));
      tooltip.accept(Component.translatable("tooltip.energyhatch.out.transfer", size.transferLimit).withStyle(ChatFormatting.GRAY));
    }
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new EnergyOutputHatchEntity(pos, state, size);
  }
}