package es.degrassi.mmr.common.block;

import es.degrassi.mmr.common.block.base.BlockFluidHatch;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.entity.FluidInputHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockFluidInputHatch extends BlockFluidHatch {
  public BlockFluidInputHatch(Properties props, FluidHatchSize size) {
    super(props, size);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new FluidInputHatchEntity(blockPos, blockState, size);
  }
}
