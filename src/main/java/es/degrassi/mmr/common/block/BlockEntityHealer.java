package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BaseEntityBlock;
import es.degrassi.mmr.common.entity.EntityHealerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityHealer extends BaseEntityBlock {
  public BlockEntityHealer(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockEntityHealer> codec() {
    return BlockCodecs.ENTITY_HEALER.mapCodec();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new EntityHealerEntity(blockPos, blockState);
  }
}
