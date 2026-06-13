package es.degrassi.mmreborn.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmreborn.client.util.RenderTypes;
import es.degrassi.mmreborn.common.item.StructureCreatorItem;
import es.degrassi.mmreborn.common.item.StructureTemplateItem;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
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
        var templateItem = item == ItemRegistration.STRUCTURE_TEMPLATE_ITEM.get() ? player.getMainHandItem() : null;
        if (templateItem == null) {
          var slot = StructureCreatorItem.findSlotMatchingItem(player.getInventory(), ItemRegistration.STRUCTURE_TEMPLATE_ITEM.toStack());
          if (slot == -1) return;
          templateItem = player.getInventory().getItem(slot);
        }
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderTypes.THICK_LINES);
        Vec3 playerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Set<BlockPos> blocks = StructureTemplateItem.getSelectedBlocks(templateItem);
        blocks.forEach(pos -> {
          AABB box = new AABB(pos);
          pose.pushPose();
          pose.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
          LevelRenderer.renderLineBox(pose, builder, box, 1.0F, 0.0F, 0.0F, 1.0F);
          pose.popPose();
        });
        buffer.endBatch(RenderTypes.THICK_LINES);
      }
    }
  }
}
