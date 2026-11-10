package es.degrassi.mmr.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.StructureCheckerEntity;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class StructureCheckerRenderer {
  private static final Set<Preview> previews = new ObjectOpenHashSet<>();

  record Preview(
      Identifier machine,
      StructureRenderer renderer
  ) {
    void submit(PoseStack stack, SubmitNodeCollector collector, Direction facing, Level level, BlockPos controller) {
      renderer.render(stack, collector, facing, level, controller);
    }

    boolean shouldRender() {
      return renderer.shouldRender();
    }

    boolean shouldRender(Identifier toCheck) {
      return toCheck.equals(machine);
    }
  }

  public static void submit(PoseStack poseStack, SubmitNodeCollector collector) {
    previews.removeIf(preview -> !preview.shouldRender());
    var mc = Minecraft.getInstance();
    var level = mc.level;
    if (level == null) return;
    var player = mc.player;
    if (player == null) return;
    AABB area = new AABB(player.blockPosition()).inflate(8);
    var pos = new BlockPos.MutableBlockPos();
    Vec3 camera = mc.gameRenderer.getMainCamera().position();
    for (int i = (int) area.minX; i < (int) area.maxX; i++) {
      pos.setX(i);
      for (int j = (int) area.minZ; j < (int) area.maxZ; j++) {
        pos.setZ(j);
        for (int w = (int) area.minY; w < (int) area.maxY; w++) {
          pos.setY(w);
          if (!(level.getBlockEntity(pos.immutable()) instanceof StructureCheckerEntity entity)) continue;
          if (entity.getBoundMachine() == null) continue;
          for (var preview : previews) {
            if (!preview.shouldRender(entity.getBoundMachine().getRegistryName())) continue;
            poseStack.pushPose();
            poseStack.translate(
                pos.getX() - camera.x,
                pos.getY() - camera.y,
                pos.getZ() - camera.z
            );
            preview.submit(poseStack, collector, entity.getControllerFacing(), level, pos);
            poseStack.popPose();
          }
        }
      }
    }
  }

  public static void add(Identifier machine, Structure structure) {
    previews.add(new Preview(machine, new StructureRenderer(MMRConfig.get().structureRenderTime.get(), structure::getBlocks)));
    // renderers.put(machine, );
  }
}
