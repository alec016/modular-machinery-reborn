package es.degrassi.mmr.common.block.base;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.client.screen.EnergyHatchUI;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import es.degrassi.mmr.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmr.common.util.RedstoneHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class BlockEnergyHatch extends BlockMachineComponent implements BlockTickEntity, TooltipAppendable, BlockUIMenuType.BlockUI {

  @Override
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof EnergyHatchEntity entity) {
      return EnergyHatchUI.create(holder, entity);
    }
    return null;
  }
  @Getter
  protected final EnergyHatchSize size;
  public BlockEnergyHatch(Properties id, EnergyHatchSize type) {
    super(id);
    this.size = type;
  }

  @Override
  protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof EnergyHatchEntity entity) {
      if (player instanceof ServerPlayer serverPlayer) {
        BlockUIMenuType.openUI(serverPlayer, pos);
      }
      return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction side) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }
}