package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BaseEntityBlock;
import es.degrassi.mmr.common.entity.EntityDamagerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityDamager extends BaseEntityBlock {
  public BlockEntityDamager(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockEntityDamager> codec() {
    return BlockCodecs.ENTITY_DAMAGER.mapCodec();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new EntityDamagerEntity(blockPos, blockState);
  }
}
