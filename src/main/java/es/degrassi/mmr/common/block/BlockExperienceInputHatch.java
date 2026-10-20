package es.degrassi.mmr.common.block;

import es.degrassi.mmr.common.block.base.BlockExperienceHatch;
import es.degrassi.mmr.common.block.base.BlockTickEntity;
import es.degrassi.mmr.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmr.common.entity.ExperienceInputHatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class BlockExperienceInputHatch extends BlockExperienceHatch implements BlockTickEntity {
  public BlockExperienceInputHatch(Properties id, ExperienceHatchSize size) {
    super(id, size);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new ExperienceInputHatchEntity(blockPos, blockState, size);
  }
}
