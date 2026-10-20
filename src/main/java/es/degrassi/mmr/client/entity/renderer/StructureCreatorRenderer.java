package es.degrassi.mmr.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmr.client.util.RenderTypes;
import es.degrassi.mmr.common.item.StructureCreatorItem;
import es.degrassi.mmr.common.item.StructureTemplateItem;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class StructureCreatorRenderer {
  private StructureCreatorRenderer() {}

  public static void renderSelectedBlocks(PoseStack pose) {
    var player = Minecraft.getInstance().player;
    if(player != null) {
      var item = player.getMainHandItem().getItem();
      if (
          item == ItemRegistration.STRUCTURE_CREATOR_ITEM_SINGLE.get() ||
          item == ItemRegistration.STRUCTURE_CREATOR_ITEM_BOX.get() ||
          item == ItemRegistration.STRUCTURE_CREATOR_ITEM_VEIN.get() ||
          item == ItemRegistration.STRUCTURE_TEMPLATE_ITEM.get()
      ) {
        var collector = new SubmitNodeStorage();
        collector.submitCustomGeometry(pose, RenderTypes.THICK_LINES, (p, builder) -> {
          var templateItem = item == ItemRegistration.STRUCTURE_TEMPLATE_ITEM.get() ? player.getMainHandItem() : null;
          if (templateItem == null) {
            var slot = StructureCreatorItem.findSlotMatchingItem(player.getInventory(), ItemRegistration.STRUCTURE_TEMPLATE_ITEM.stack(1));
            if (slot == -1) return;
            templateItem = player.getInventory().getItem(slot);
          }
          Vec3 playerPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
          Set<BlockPos> blocks = StructureTemplateItem.getSelectedBlocks(templateItem);
          blocks.forEach(pos -> {
            AABB box = new AABB(pos);
            pose.pushPose();
            pose.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
            renderLineBox(p, builder, box, 1.0F, 0.0F, 0.0F, 1.0F);
            pose.popPose();
          });
        });
        collector.endFrame();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        buffer.endBatch(RenderTypes.THICK_LINES);
      }
    }
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
    consumer.addVertex(posestack$pose, f, f1, f2).setColor(red, green2, blue2, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green2, blue2, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f1, f2).setColor(red2, green, blue2, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f4, f2).setColor(red2, green, blue2, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f1, f2).setColor(red2, green2, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    consumer.addVertex(posestack$pose, f, f1, f5).setColor(red2, green2, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
    consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
    consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
  }
}
