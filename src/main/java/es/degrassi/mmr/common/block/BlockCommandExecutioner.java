package es.degrassi.mmr.common.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.client.screen.CommandExecutionerUI;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.entity.CommandExecutionerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockCommandExecutioner extends BlockMachineComponent implements BlockUIMenuType.BlockUI {
  public BlockCommandExecutioner(Properties id) {
    super(id);
  }

  @Override
  protected MapCodec<BlockCommandExecutioner> codec() {
    return BlockCodecs.COMMAND_EXECUTIONER.mapCodec();
  }

  @Override
  @Nullable
  public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
    if (holder.player.level().getBlockEntity(holder.pos) instanceof CommandExecutionerEntity entity) {
      return CommandExecutionerUI.create(holder, entity);
    }
    return null;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new CommandExecutionerEntity(blockPos, blockState);
  }
}
