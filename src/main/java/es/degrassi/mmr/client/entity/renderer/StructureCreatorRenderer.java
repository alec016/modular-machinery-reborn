package es.degrassi.mmr.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmr.client.util.RenderTypes;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.item.StructureCreatorItem;
import es.degrassi.mmr.common.item.StructureTemplateItem;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;

import java.util.Set;

public class StructureCreatorRenderer {
  private StructureCreatorRenderer() {}

  /**
   * Called from {@code SubmitCustomGeometryEvent}: geometry goes into the level's own collector
   * (a private {@code SubmitNodeStorage} is never rendered by the vanilla pipeline) and the pose
   * stack is already camera-relative, so no camera translation is applied here.
   */
  public static void submitSelectedBlocks(SubmitNodeCollector collector, PoseStack pose) {
    var player = Minecraft.getInstance().player;
    if (player == null) {
      return;
    }
    var item = player.getMainHandItem().getItem();
    if (item != ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE.get()
        && item != ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX.get()
        && item != ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN.get()
        && item != ItemRegistration.STRUCTURE_TEMPLATE_ITEM.get()) {
      return;
    }
    var templateItem = item == ItemRegistration.STRUCTURE_TEMPLATE_ITEM.get() ? player.getMainHandItem() : null;
    if (templateItem == null) {
      var slot = StructureCreatorItem.findSlotMatchingItem(player.getInventory(), ItemRegistration.STRUCTURE_TEMPLATE_ITEM.stack(1));
      if (slot == -1) return;
      templateItem = player.getInventory().getItem(slot);
    }
    Set<BlockPos> blocks = StructureTemplateItem.getSelectedBlocks(templateItem);
    if (blocks.isEmpty()) {
      return;
    }
    // The level pose stack is world-space (vanilla translates every block entity by
    // blockPos - camera before submitting), so the camera offset goes on here.
    Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().position();
    int color = MMRConfig.get().structureCreatorColor.get();
    float red = ARGB.red(color) / 255F, green = ARGB.green(color) / 255F,
        blue = ARGB.blue(color) / 255F, alpha = ARGB.alpha(color) / 255F;
    pose.pushPose();
    pose.translate(-camera.x(), -camera.y(), -camera.z());
    collector.submitCustomGeometry(pose, RenderTypes.THICK_LINES, (linePose, builder) -> {
      blocks.forEach(pos -> renderLineBox(linePose, builder, new AABB(pos), red, green, blue, alpha));
    });
    pose.popPose();
  }

  private static void renderLineBox(PoseStack.Pose poseStack, VertexConsumer buffer, AABB box, float red, float green, float blue,
                                    float alpha) {
    renderLineBox(poseStack, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
  }

  private static void renderLineBox(PoseStack.Pose posestack$pose, VertexConsumer consumer,
                                    double minX, double minY, double minZ,
                                    double maxX, double maxY, double maxZ,
                                    float red, float green, float blue, float alpha,
                                    float red2, float green2, float blue2
  ) {
    float f = (float)minX;
    float f1 = (float)minY;
    float f2 = (float)minZ;
    float f3 = (float)maxX;
    float f4 = (float)maxY;
    float f5 = (float)maxZ;
    consumer.addVertex(posestack$pose, f, f1, f2).setColor(red, green2, blue2, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green2, blue2, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f1, f2).setColor(red2, green, blue2, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f4, f2).setColor(red2, green, blue2, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f1, f2).setColor(red2, green2, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f1, f5).setColor(red2, green2, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F).setLineWidth(2F);
    consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F).setLineWidth(2F);
  }
}
