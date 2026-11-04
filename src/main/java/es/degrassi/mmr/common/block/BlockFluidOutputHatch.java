package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BlockFluidHatch;
import es.degrassi.mmr.common.block.prop.FluidHatchSize;
import es.degrassi.mmr.common.entity.FluidOutputHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockFluidOutputHatch extends BlockFluidHatch {
  public BlockFluidOutputHatch(Properties id, FluidHatchSize size) {
    super(id, size);
  }

  @Override
  protected MapCodec<BlockFluidOutputHatch> codec() {
    return BlockCodecs.FLUID_OUTPUT.mapCodec();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new FluidOutputHatchEntity(blockPos, blockState, size);
  }
}
