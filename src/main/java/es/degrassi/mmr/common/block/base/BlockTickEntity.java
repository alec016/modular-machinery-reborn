package es.degrassi.mmr.common.block.base;

import es.degrassi.mmr.api.entity.ITickEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockTickEntity extends EntityBlock {
  @Nullable
  @Override
  default <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
    return (level, pos, state, blockEntity) -> {
      if (blockEntity.getType() == pBlockEntityType && blockEntity instanceof ITickEntity entity) {
        entity.tick();
      }
    };
  }
}
