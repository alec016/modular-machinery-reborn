package es.degrassi.mmr.api.controller;

import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IMultiblockController {

  default MachineControllerEntity self() {
    return (MachineControllerEntity) this;
  }

  void asyncCheckPattern(long periodID);

  Identifier getId();

  void onStructureFormed();

  void onStructureUnformed();

  default DynamicMachine getController() {
    return self().getFoundMachine();
  }

  BlockPos getBlockPos();

  Direction getFacing();

  BlockState getBlockState();

  Level getLevel();

  boolean isPosInCache(BlockPos pos);

  void onBlockStateChanged(BlockPos pos, BlockState newState);

  default boolean isPause() {
    return getLevel().getServer().isPaused();
  }
}
