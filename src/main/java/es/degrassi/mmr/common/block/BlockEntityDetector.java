package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BaseEntityBlock;
import es.degrassi.mmr.common.entity.EntityDetectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityDetector extends BaseEntityBlock {
  public BlockEntityDetector(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockEntityDetector> codec() {
    return BlockCodecs.ENTITY_DETECTOR.mapCodec();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new EntityDetectorEntity(blockPos, blockState);
  }
}
