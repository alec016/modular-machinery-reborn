package es.degrassi.mmr.common.util;

import com.google.common.collect.Lists;
import es.degrassi.mmr.common.entity.ChunkloaderEntity;
import net.minecraft.world.level.ChunkPos;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ChunkloaderList {
  private ChunkloaderList() {}

  private static final List<WeakReference<ChunkloaderEntity>> LOADED_MACHINES = Collections.synchronizedList(Lists.newArrayList());

  public static void add(ChunkloaderEntity tile) {
    if (tile.getLevel() != null && !tile.getLevel().isClientSide())
      LOADED_MACHINES.add(new WeakReference<>(tile));
  }

  public static Optional<ChunkloaderEntity> findInSameChunk(ChunkloaderEntity machine) {
    return getLoadedMachines()
        .stream()
        .filter(tile -> tile != machine && tile.getLevel() == machine.getLevel() && ChunkPos.containing(tile.getBlockPos()).equals(ChunkPos.containing(machine.getBlockPos())))
        .findFirst();
  }

  public static List<ChunkloaderEntity> getLoadedMachines() {
    Iterator<WeakReference<ChunkloaderEntity>> iterator = LOADED_MACHINES.iterator();
    List<ChunkloaderEntity> loadedMachines = Lists.newArrayList();
    while (iterator.hasNext()) {
      ChunkloaderEntity tile = iterator.next().get();
      if (tile == null || tile.isRemoved())
        iterator.remove();
      else
        loadedMachines.add(tile);
    }
    return loadedMachines;
  }
}
