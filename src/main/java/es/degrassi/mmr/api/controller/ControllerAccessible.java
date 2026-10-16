package es.degrassi.mmr.api.controller;

import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public interface ControllerAccessible {
  BlockPos getControllerPos();

  void setControllerPos(BlockPos pos);

  @Nullable
  Level getLevel();

  default Direction getControllerFacing() {
    return Optional.ofNullable(getController()).map(MachineControllerEntity::getFacing).orElse(Direction.NORTH);
  }

  @Nullable
  default MachineControllerEntity getController() {
    if (getLevel() != null && getControllerPos() != null && getLevel().getBlockEntity(getControllerPos()) instanceof MachineControllerEntity entity) {
      return entity;
    }
    return null;
  }

  default Map<BlockPos, Optional<MachineComponent<?>>> getFoundComponents() {
    if (getController() == null) return Map.of();
    return getController().getFoundComponentsMap();
  }
}
