package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BaseEntityBlock;
import es.degrassi.mmr.common.entity.EntitySpawnerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntitySpawner extends BaseEntityBlock {
  public BlockEntitySpawner(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockEntitySpawner> codec() {
    return BlockCodecs.ENTITY_SPAWNER.mapCodec();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new EntitySpawnerEntity(blockPos, blockState);
  }
}
