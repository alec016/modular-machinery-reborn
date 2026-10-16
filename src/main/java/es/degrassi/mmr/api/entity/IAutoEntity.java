package es.degrassi.mmr.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import javax.annotation.Nullable;
import java.util.Map;

public interface IAutoEntity<T> {
  /* Block Entity Stuff */
  boolean isRemoved();
  BlockPos getBlockPos();
  @Nullable
  Level getLevel();
  void setChanged();

  boolean shouldAuto();

  Map<Direction, BlockCapabilityCache<T, Direction>> getNeighbourStorages();

  @Nullable
  default T getNeighbour(BlockCapability<T, Direction> cap, Direction side) {
    if(getNeighbourStorages().get(side) == null)
      getNeighbourStorages()
          .put(
              side,
              BlockCapabilityCache.create(
                  cap,
                  (ServerLevel)this.getLevel(),
                  this.getBlockPos().relative(side),
                  side.getOpposite(),
                  () -> !this.isRemoved(),
                  () -> getNeighbourStorages().remove(side)
              )
          );
    return getNeighbourStorages().get(side).getCapability();
  }
}
