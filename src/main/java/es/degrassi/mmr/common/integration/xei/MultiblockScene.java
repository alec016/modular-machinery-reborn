package es.degrassi.mmr.common.integration.xei;


import com.lowdragmc.lowdraglib2.utils.virtuallevel.DummyWorld;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.TrackedDummyWorld;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.PartialBlockState;
import es.degrassi.mmr.api.entity.ColorableMachineEntity;
import es.degrassi.mmr.api.entity.TextureableMachineEntity;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.util.CycleTimer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiblockScene {
  public static final CycleTimer blockTimer = new CycleTimer(MMRConfig.get().blockTagCycleTime, true);

  public static DummyWorld createTestScene(DynamicMachine structure) {
    var dummyWorld = new TrackedDummyWorld();
    blockTimer.onDraw();
    structure.getPattern()
        .getBlocksFiltered(Direction.NORTH)
        .forEach((pos, ing) -> {
          if (ing.isNot()) {
            var list = BuiltInRegistries.BLOCK.stream()
                .filter(state -> ing.getAll().stream().noneMatch(partial -> partial.getBlockState().is(state)))
                .map(Block::defaultBlockState)
                .filter(state -> ing.getTags().stream().noneMatch(state::is))
                .map(state -> new PartialBlockState(state, new ArrayList<>(), null))
                .toList();
            var partialState = blockTimer.get(list);
            if (partialState == null) return;
            dummyWorld.setBlockAndUpdate(pos, partialState.getBlockState());
            return;
          }
          var partialState = blockTimer.get(ing.getAll());
          if (partialState == null) return;
          dummyWorld.setBlockAndUpdate(pos, partialState.getBlockState());
        });
    var controllerPos = new BlockPos(0, 0, 0);
    var controller = BlockRegistration.CONTROLLER.block().defaultBlockState();
    MachineControllerEntity entity = new MachineControllerEntity(new BlockPos(0, 0, 0), controller);
    dummyWorld.setBlockAndUpdate(controllerPos, controller);
    entity.setLevel(dummyWorld);
    if (dummyWorld.getBlockEntity(controllerPos) instanceof MachineControllerEntity e) {
      e.setMachine(structure.getRegistryName());
      e.setFormed(true);
      e.setChanged();
      for (long packed : dummyWorld.getFilledBlocks()) {
        var be = dummyWorld.getBlockEntity(BlockPos.of(packed));
        if (be != null && be.getLevel() == null) {
          be.setLevel(dummyWorld);
        }
        if (be instanceof ColorableMachineEntity ce) {
          ce.setMachineColor(structure.getMachineColor());
        }
      }

      entity.getFoundMachine()
          .getPattern()
          .getBlocks(entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
          .entrySet()
          .parallelStream()
          .filter(en -> !en.getValue().equals(BlockIngredient.MACHINE))
          .map(Map.Entry::getKey)
          .map(controllerPos::offset)
          .forEach(p -> tryColorize(e, p));
    }
    return dummyWorld;
  }

  public static void tick(DummyWorld scene, DynamicMachine structure) {
    blockTimer.onDraw();
    structure.getPattern()
        .getBlocksFiltered(Direction.NORTH)
        .forEach((pos, ing) -> {
          if (ing.isNot()) {
            var list = BuiltInRegistries.BLOCK.stream()
                .filter(state -> ing.getAll().stream().noneMatch(partial -> partial.getBlockState().is(state)))
                .map(Block::defaultBlockState)
                .filter(state -> ing.getTags().stream().noneMatch(state::is))
                .map(state -> new PartialBlockState(state, new ArrayList<>(), null))
                .toList();
            var partialState = blockTimer.get(list);
            if (partialState == null) return;
            scene.setBlockAndUpdate(pos, partialState.getBlockState());
            return;
          }
          var partialState = blockTimer.get(ing.getAll());
          if (partialState == null) return;
          scene.setBlockAndUpdate(pos, partialState.getBlockState());
        });
    var controllerPos = new BlockPos(0, 0, 0);
    var controller = BlockRegistration.CONTROLLER.block().defaultBlockState();
    MachineControllerEntity entity = new MachineControllerEntity(new BlockPos(0, 0, 0), controller);
    scene.setBlockAndUpdate(controllerPos, controller);
    entity.setLevel(scene);
    if (scene.getBlockEntity(controllerPos) instanceof MachineControllerEntity e) {
      e.setMachine(structure.getRegistryName());
      e.setFormed(true);
      e.setChanged();
      for (long packed : scene.getFilledBlocks()) {
        var be = scene.getBlockEntity(BlockPos.of(packed));
        if (be != null && be.getLevel() == null) {
          be.setLevel(scene);
        }
        if (be instanceof ColorableMachineEntity ce) {
          ce.setMachineColor(structure.getMachineColor());
        }
      }

      entity.getFoundMachine()
          .getPattern()
          .getBlocks(entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
          .entrySet()
          .parallelStream()
          .filter(en -> !en.getValue().equals(BlockIngredient.MACHINE))
          .map(Map.Entry::getKey)
          .map(controllerPos::offset)
          .forEach(p -> tryColorize(e, p));
    }
  }

  private static void tryColorize(MachineControllerEntity controller, BlockPos pos) {
    if (controller.getLevel() == null) return;
    BlockEntity te = controller.getLevel().getBlockEntity(pos);
    AtomicBoolean shouldColor = new AtomicBoolean(true);
    if (te instanceof TextureableMachineEntity entity) {
      entity.resetTextures();
      Optional.ofNullable(controller.getFoundMachine().getFormedTextures().get(entity.getHatchType())).ifPresent(p -> {
        shouldColor.set(p.getFirst());
        p.mapSecond(pair -> {
          pair.getFirst().ifPresent(entity::setMachineBaseTexture);
          pair.getSecond().ifPresent(entity::setMachineOverlayTexture);
          return null;
        });
      });
    }

    if (te instanceof ColorableMachineEntity entity) {
      entity.setMachineColor(shouldColor.get() ? controller.getMachineColor() : 0xffffffff);
    }
  }
}
