package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import es.degrassi.mmr.client.screen.RedstonePortUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockRedstonePort extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockRedstonePort(Properties id) {
    super(id);
  }

  @Override
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof RedstonePortEntity entity) {
      return RedstonePortUI.create(holder, entity);
    }
    return null;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new RedstonePortEntity(blockPos, blockState);
  }

  @Override
  protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof RedstonePortEntity entity) {
      if (player instanceof ServerPlayer serverPlayer) {
        BlockUIMenuType.openUI(serverPlayer, pos);
      }
      return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  public boolean isSignalSource(BlockState state) {
    return true;
  }

  @Override
  public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    if (level.getBlockEntity(pos) instanceof RedstonePortEntity entity) {
      return entity.getOutputAmount();
    }
    return super.getDirectSignal(state, level, pos, side);
  }

  @Override
  protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    if (level.getBlockEntity(pos) instanceof RedstonePortEntity entity) {
      return entity.getOutputAmount();
    }
    return super.getSignal(state, level, pos, direction);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction side) {
    if (level.getBlockEntity(pos) instanceof RedstonePortEntity entity) {
      return entity.getOutputAmount();
    }
    return super.getAnalogOutputSignal(state, level, pos, side);
  }
}
