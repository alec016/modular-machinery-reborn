package es.degrassi.mmr.common.block;

import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockStructureChecker extends BlockMachineComponent {
  public BlockStructureChecker(Properties id) {
    super(id);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new StructureCheckerEntity(blockPos, blockState);
  }
}
