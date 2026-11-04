package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.TimeCounterUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.TimeCounterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockTimeCounter extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockTimeCounter(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockTimeCounter> codec() {
    return BlockCodecs.TIME_COUNTER.mapCodec();
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof TimeCounterEntity entity) {
      return TimeCounterUI.create(holder, entity);
    }
    return null;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TimeCounterEntity(pos, state);
  }
}
