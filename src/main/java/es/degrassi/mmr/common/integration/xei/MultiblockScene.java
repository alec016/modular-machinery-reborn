package es.degrassi.mmr.common.integration.xei;


import com.lowdragmc.lowdraglib2.utils.virtuallevel.DummyWorld;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.TrackedDummyWorld;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.util.CycleTimer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class MultiblockScene {
  public static final CycleTimer blockTimer = new CycleTimer(MMRConfig.get().blockTagCycleTime, true);

  public static DummyWorld createTestScene(DynamicMachine structure) {
    var dummyWorld = new TrackedDummyWorld();
    blockTimer.onDraw();
    structure.getPattern()
        .getBlocksFiltered(Direction.NORTH)
        .forEach((pos, ing) -> {
          var partialState = blockTimer.get(ing.getAll());
          if (partialState == null) return;
          dummyWorld.setBlockAndUpdate(pos, partialState.getBlockState());
        });
    var controllerPos = new BlockPos(0, 0, 0);
    var controller = BlockRegistration.CONTROLLER.get().defaultBlockState();
    MachineControllerEntity entity = new MachineControllerEntity(new BlockPos(0, 0, 0), controller);
    dummyWorld.setBlockAndUpdate(controllerPos, controller);
    entity.setLevel(dummyWorld);
    if (dummyWorld.getBlockEntity(controllerPos) instanceof MachineControllerEntity e) {
      e.setMachine(structure.getRegistryName());
    }
    return dummyWorld;
  }

  public static void tick(DummyWorld scene, DynamicMachine structure) {
    blockTimer.onDraw();
    structure.getPattern()
        .getBlocksFiltered(Direction.NORTH)
        .forEach((pos, ing) -> {
          var partialState = blockTimer.get(ing.getAll());
          if (partialState == null) return;
          scene.setBlockAndUpdate(pos, partialState.getBlockState());
        });
    var controllerPos = new BlockPos(0, 0, 0);
    var controller = BlockRegistration.CONTROLLER.get().defaultBlockState();
    MachineControllerEntity entity = new MachineControllerEntity(new BlockPos(0, 0, 0), controller);
    scene.setBlockAndUpdate(controllerPos, controller);
    entity.setLevel(scene);
    if (scene.getBlockEntity(controllerPos) instanceof MachineControllerEntity e) {
      e.setMachine(structure.getRegistryName());
    }
  }
}
