package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.ChunkloaderUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.block.base.BlockTickEntity;
import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockChunkloader extends BlockMachineComponent implements BlockTickEntity, BlockUIMenuType.BlockUI {
  public BlockChunkloader(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockChunkloader> codec() {
    return BlockCodecs.CHUNKLOADER.mapCodec();
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof ChunkloaderEntity entity) {
      return ChunkloaderUI.create(holder, entity);
    }
    return null;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ChunkloaderEntity(pos, state);
  }

}
