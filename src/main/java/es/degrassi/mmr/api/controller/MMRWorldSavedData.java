package es.degrassi.mmr.api.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.util.MMRLogger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MMRWorldSavedData extends SavedData {
  public static final SavedDataType<MMRWorldSavedData> TYPE = new SavedDataType<>(
      ModularMachineryReborn.rl("mmr_multiblocks"),
      MMRWorldSavedData::new,
      serverLevel -> MapCodec.unitCodec(() -> new MMRWorldSavedData(serverLevel)),
      DataFixTypes.LEVEL
  );

  public static MMRWorldSavedData getOrCreate(ServerLevel serverLevel) {
    return serverLevel.getDataStorage().computeIfAbsent(TYPE);
  }

  public final Map<BlockPos, IMultiblockController> mapping;
  public final Map<ChunkPos, Set<IMultiblockController>> chunkPosMapping;

  private MMRWorldSavedData(ServerLevel level) {
    this.mapping = new Object2ObjectOpenHashMap<>();
    this.chunkPosMapping = new HashMap<>();
  }

  public Set<IMultiblockController> getControllersInChunk(ChunkPos chunkPos) {
    return chunkPosMapping.getOrDefault(chunkPos, Collections.emptySet());
  }

  public void addMapping(IMultiblockController machine) {
    this.mapping.put(machine.getBlockPos(), machine);
    for (var relative : machine.getController().getPattern().getPattern().get(machine.getFacing()).keySet()) {
      var pos = machine.getBlockPos().offset(relative);
      chunkPosMapping.computeIfAbsent(ChunkPos.containing(pos), c -> new HashSet<>()).add(machine);
    }
  }

  public void removeMapping(IMultiblockController machine) {
    var prev = this.mapping.remove(machine.getBlockPos());
    if (prev == null) return;
    for (var relative : machine.getController().getPattern().getPattern().get(machine.getFacing()).keySet()) {
      var pos = machine.getBlockPos().offset(relative);
      var cPos = ChunkPos.containing(pos);
      var set = chunkPosMapping.get(cPos);
      if (set == null) continue;
      set.remove(machine);
      chunkPosMapping.put(cPos, set);
    }
  }

  private final CopyOnWriteArrayList<IMultiblockController> controllers = new CopyOnWriteArrayList<>();
  private ScheduledExecutorService executorService;
  private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
      .setNameFormat("MMR Multiblock Async Thread-%d")
      .setDaemon(true)
      .build();
  private static final ThreadLocal<Boolean> IN_SERVICE = ThreadLocal.withInitial(() -> false);
  @Getter
  private long periodID = Long.MIN_VALUE;

  public void createExecutorService() {
    if (executorService != null && !executorService.isShutdown()) return;
    executorService = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY);
    executorService.scheduleAtFixedRate(this::searchingTask,  0, 250, TimeUnit.MILLISECONDS);
  }

  public void addAsyncLogic(IMultiblockController controller) {
    controllers.add(controller);
    createExecutorService();
  }

  public void removeAsyncLogic(IMultiblockController controller) {
    if (controllers.contains(controller)) {
      controllers.remove(controller);
      if (controllers.isEmpty()) {
        releaseExecutorService();
      }
    }
  }

  private void searchingTask() {
    try {
      if (!ModularMachineryReborn.canGetServerLevel()) return;
      IN_SERVICE.set(true);
      for (var controller : controllers) {
        try {
          if (controller.isPause()) return;
          controller.asyncCheckPattern(periodID);
        } catch(Throwable e) {
          MMRLogger.INSTANCE.error("Error while assembling multiblock {}", controller.getId(), e);
        }
      }
    } catch(Throwable e) {
      MMRLogger.INSTANCE.error("Error while assembling multiblocks", e);
    } finally {
      IN_SERVICE.set(false);
    }
    periodID++;
  }

  public static boolean isThreadService() {
    return IN_SERVICE.get() && ModularMachineryReborn.canGetServerLevel();
  }

  public void releaseExecutorService() {
    if (executorService != null) {
      executorService.shutdownNow();
    }
    executorService = null;
  }

  public boolean containsAsyncLogicOrMapping(MachineControllerEntity machineControllerEntity) {
    return controllers.contains(machineControllerEntity) || mapping.containsValue(machineControllerEntity);
  }
}
