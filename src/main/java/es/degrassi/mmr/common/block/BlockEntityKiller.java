package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BaseEntityBlock;
import es.degrassi.mmr.common.entity.EntityKillerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityKiller extends BaseEntityBlock {
  public BlockEntityKiller(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockEntityKiller> codec() {
    return BlockCodecs.ENTITY_KILLER.mapCodec();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new EntityKillerEntity(blockPos, blockState);
  }
}
