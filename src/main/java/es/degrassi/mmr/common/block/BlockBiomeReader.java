package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.BiomeReaderUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.BiomeReaderEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockBiomeReader extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockBiomeReader(Properties id) {
    super(id);
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof BiomeReaderEntity entity) {
      return BiomeReaderUI.create(holder, entity);
    }
    return null;
  }

  @Override
  protected MapCodec<BlockBiomeReader> codec() {
    return BlockCodecs.BIOME_READER.mapCodec();
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BiomeReaderEntity(pos, state);
  }
}
