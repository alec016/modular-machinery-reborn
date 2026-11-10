package es.degrassi.mmr.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class ControllerRenderer {
  public static void remove(BlockPos controllerPos) {
    previews.removeIf(preview -> preview.shouldRender(controllerPos));
  }

  record Preview(BlockPos controller, StructureRenderer structure) {
    void submit(PoseStack stack, SubmitNodeCollector collector, Direction facing, Level level) {
      structure.render(stack, collector, facing, level, controller);
    }

    boolean shouldRender() {
      return structure.shouldRender();
    }

    boolean shouldRender(BlockPos toCheck) {
      return toCheck.equals(controller);
    }
  }
  private static final Set<Preview> previews = new ObjectOpenHashSet<>();

  public static void submit(PoseStack pose, SubmitNodeCollector collector) {
    previews.removeIf(preview -> !preview.shouldRender());
    var mc = Minecraft.getInstance();
    var level = mc.level;
    if (level == null || previews.isEmpty()) return;
    Vec3 camera = mc.gameRenderer.getMainCamera().position();
    for (var preview : previews) {
      if (!(level.getBlockEntity(preview.controller) instanceof MachineControllerEntity machineControllerEntity)) continue;
      var facing = machineControllerEntity.getFacing();
      var pos = machineControllerEntity.getBlockPos();
      if (preview.shouldRender(pos)) {
        pose.pushPose();
        pose.translate(pos.getX() - camera.x(), pos.getY() - camera.y(), pos.getZ() - camera.z());
        preview.submit(pose, collector, facing, level);
        pose.popPose();
      }
    }
  }

  public static void add(DynamicMachine machine, BlockPos controllerPos) {
    previews.add(new Preview(controllerPos, new StructureRenderer(MMRConfig.get().structureRenderTime.get(), machine.getPattern()::getBlocks)));
  }
}
