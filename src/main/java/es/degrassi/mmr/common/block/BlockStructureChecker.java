package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.StructureCheckerUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockStructureChecker extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockStructureChecker(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockStructureChecker> codec() {
    return BlockCodecs.STRUCTURE_CHECKER.mapCodec();
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof StructureCheckerEntity entity) {
      return StructureCheckerUI.create(holder, entity);
    }
    return null;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new StructureCheckerEntity(blockPos, blockState);
  }
}
