package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BaseItemBus;
import es.degrassi.mmr.common.block.prop.ItemBusSize;
import es.degrassi.mmr.common.entity.ItemOutputBusEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockOutputBus extends BaseItemBus {

  public BlockOutputBus(Properties id, ItemBusSize size) {
    super(id, size);
  }

  @Override
  protected MapCodec<BlockOutputBus> codec() {
    return BlockCodecs.ITEM_OUTPUT_BUS.mapCodec();
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new ItemOutputBusEntity(blockPos, blockState, getSize());
  }
}
