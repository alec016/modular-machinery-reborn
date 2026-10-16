package es.degrassi.mmr.common.block;

import es.degrassi.mmr.common.block.base.BlockExperienceHatch;
import es.degrassi.mmr.common.block.base.BlockTickEntity;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.entity.ExperienceOutputHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockExperienceOutputHatch extends BlockExperienceHatch implements BlockTickEntity {
  public BlockExperienceOutputHatch(ExperienceHatchSize size) {
    super(size);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new ExperienceOutputHatchEntity(blockPos, blockState, size);
  }
}
