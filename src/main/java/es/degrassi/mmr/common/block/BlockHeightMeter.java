package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.HeightMeterUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.HeightMeterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class BlockHeightMeter extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockHeightMeter(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockHeightMeter> codec() {
    return BlockCodecs.HEIGHT_METER.mapCodec();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING,
        context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof HeightMeterEntity entity) {
      return HeightMeterUI.create(holder, entity);
    }
    return null;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HeightMeterEntity(pos, state);
  }
}
