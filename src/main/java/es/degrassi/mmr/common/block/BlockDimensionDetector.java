package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.DimensionalDetectorUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.DimensionalDetectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;


public class BlockDimensionDetector extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockDimensionDetector(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockDimensionDetector> codec() {
    return BlockCodecs.DIMENSION_DETECTOR.mapCodec();
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof DimensionalDetectorEntity entity) {
      return DimensionalDetectorUI.create(holder, entity);
    }
    return null;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new DimensionalDetectorEntity(pos, state);
  }

}
