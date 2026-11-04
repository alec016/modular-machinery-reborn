package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.FuelTankUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.block.base.BlockTickEntity;
import es.degrassi.mmr.common.block.base.SizedBlockHatch;
import es.degrassi.mmr.common.block.base.TooltipAppendable;
import es.degrassi.mmr.common.block.prop.FuelTankSize;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import es.degrassi.mmr.common.util.RedstoneHelper;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockFuelTank extends BlockMachineComponent implements BlockTickEntity, TooltipAppendable,
    BlockUIMenuType.BlockUI, SizedBlockHatch<FuelTankSize> {

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof FuelTankEntity entity) {
      return FuelTankUI.create(holder, entity);
    }
    return null;
  }
  @Getter
  protected final FuelTankSize size;
  public BlockFuelTank(Properties id, FuelTankSize size) {
    super(id);
    this.size = size;
  }

  @Override
  protected MapCodec<BlockFuelTank> codec() {
    return BlockCodecs.FUEL.mapCodec();
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction side) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    tooltip.accept(Component.translatable("tooltip.fueltank.storage", size.burnTimeCapacity).withStyle(ChatFormatting.GRAY));
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FuelTankEntity(pos, state, size);
  }
}
