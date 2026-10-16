package es.degrassi.mmr.common.entity.base;

import es.degrassi.mmr.api.entity.ITickEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BlockEntityRestrictedTick extends ColorableMachineComponentEntity implements ITickEntity {

  protected BlockEntityRestrictedTick(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }
}
