package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.RedstonePortUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockRedstonePort extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockRedstonePort(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockRedstonePort> codec() {
    return BlockCodecs.REDSTONE.mapCodec();
  }

  @Override
  @Nullable
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
