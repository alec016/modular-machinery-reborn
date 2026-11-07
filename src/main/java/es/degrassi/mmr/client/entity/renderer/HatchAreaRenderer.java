package es.degrassi.mmr.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import es.degrassi.mmr.client.util.RenderTypes;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.data.MMRConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HatchAreaRenderer {
  private static final float ALPHA = 1.0F;
  private static final double MAX_DISTANCE = 128.0;
  private static final double CORNER_MARK = 0.35;

  private record Preview(Identifier dimension, AABB box) {}

  /** Every preview stays active until it is toggled off, so several areas can be shown at once. */
  private static final Map<Object, Preview> previews = new LinkedHashMap<>();

  private HatchAreaRenderer() {}

  /** Shows (or replaces) the preview of the given owner. */
  public static void show(Object owner, AABB box) {
    var mc = Minecraft.getInstance();
    if (mc.level == null) {
      return;
    }
    previews.put(owner, new Preview(mc.level.dimension().identifier(), box));
  }

  public static void hide(Object owner) {
    previews.remove(owner);
  }

  /** Removes every preview (used when the world is left). */
  public static void clear() {
    previews.clear();
  }

  public static boolean isActive(Object owner) {
    return previews.containsKey(owner);
  }

  /**
   * Called from {@code SubmitCustomGeometryEvent}: the pose stack is already camera-relative, so
   * no camera translation is needed, and geometry goes into the level's own collector (a private
   * {@code SubmitNodeStorage} is never rendered by the vanilla pipeline).
   */
  public static void submit(SubmitNodeCollector collector, PoseStack pose) {
    var mc = Minecraft.getInstance();
    if (mc.level == null || previews.isEmpty()) {
      return;
    }
    var player = mc.player;
    if (player == null) {
      return;
    }
    // The level pose stack is world-space (vanilla translates every block entity by
    // blockPos - camera before submitting), so the camera offset goes on here.
    Vec3 camera = mc.gameRenderer.getMainCamera().position();
    var lineColor = configLineColor();
    var fillColor = configFillColor();
    pose.pushPose();
    pose.translate(-camera.x(), -camera.y(), -camera.z());
    for (var area : previews.values()) {
      if (!mc.level.dimension().identifier().equals(area.dimension())) {
        continue;
      }
      // Distance to the closest point of the box: a full height area (the chunk loader radius)
      // has its center far away from the player.
      var box = area.box();
      double dx = Math.max(box.minX - player.getX(), Math.max(0.0, player.getX() - box.maxX));
      double dy = Math.max(box.minY - player.getY(), Math.max(0.0, player.getY() - box.maxY));
      double dz = Math.max(box.minZ - player.getZ(), Math.max(0.0, player.getZ() - box.maxZ));
      if (dx * dx + dy * dy + dz * dz > MAX_DISTANCE * MAX_DISTANCE) {
        continue;
      }
      collector.submitCustomGeometry(pose, RenderTypes.THICK_LINES, (linePose, builder) -> {
        renderLineBox(linePose, builder, area.box(),
            ARGB.red(lineColor) / 255F, ARGB.green(lineColor) / 255F, ARGB.blue(lineColor) / 255F,
            ARGB.alpha(lineColor) / 255F);
        renderCornerMarks(linePose, builder, area.box());
      });
      collector.submitCustomGeometry(pose, net.minecraft.client.renderer.rendertype.RenderTypes.debugFilledBox(), (fillPose, builder) ->
          renderFilledBox(fillPose, builder, area.box(), fillColor));
    }
    pose.popPose();
  }

  /** Line + corner colors come from the general config (Rendering section). */
  private static int configLineColor() {
    return Config.toInt(MMRConfig.get().hatchAreaLineColor.get(), 0xffffffff);
  }

  private static int configFillColor() {
    return Config.toInt(MMRConfig.get().hatchAreaFillColor.get(), 0x40FFFFFF);
  }

  /** Six translucent faces so the preview cube reads as a solid, visible volume. Both windings are
   * emitted: the DEBUG_FILLED_BOX pipeline culls one side, and the fill must be visible from
   * inside and outside the cube alike. */
  private static void renderFilledBox(PoseStack.Pose pose, VertexConsumer builder, AABB box, int color) {
    double x0 = box.minX + 0.002, y0 = box.minY + 0.002, z0 = box.minZ + 0.002;
    double x1 = box.maxX + 0.002, y1 = box.maxY + 0.002, z1 = box.maxZ + 0.002;
    // Y- (bottom)
    quad(pose, builder, color, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
    quad(pose, builder, color, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0);
    // Y+ (top)
    quad(pose, builder, color, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
    quad(pose, builder, color, x1, y1, z0, x1, y1, z1, x0, y1, z1, x0, y1, z0);
    // Z- (north)
    quad(pose, builder, color, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0);
    quad(pose, builder, color, x1, y0, z0, x1, y1, z0, x0, y1, z0, x0, y0, z0);
    // Z+ (south)
    quad(pose, builder, color, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
    quad(pose, builder, color, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1);
    // X- (west)
    quad(pose, builder, color, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
    quad(pose, builder, color, x0, y1, z0, x0, y1, z1, x0, y0, z1, x0, y0, z0);
    // X+ (east)
    quad(pose, builder, color, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1);
    quad(pose, builder, color, x1, y0, z1, x1, y1, z1, x1, y1, z0, x1, y0, z0);
  }

  private static void quad(PoseStack.Pose pose, VertexConsumer builder, int color,
                           double x1, double y1, double z1, double x2, double y2, double z2,
                           double x3, double y3, double z3, double x4, double y4, double z4) {
    builder.addVertex(pose, (float) x1, (float) y1, (float) z1).setColor(color);
    builder.addVertex(pose, (float) x2, (float) y2, (float) z2).setColor(color);
    builder.addVertex(pose, (float) x3, (float) y3, (float) z3).setColor(color);
    builder.addVertex(pose, (float) x4, (float) y4, (float) z4).setColor(color);
  }

  private static void renderCornerMarks(PoseStack.Pose pose, VertexConsumer buffer, AABB box) {
    double[] xs = {box.minX, box.maxX};
    double[] ys = {box.minY, box.maxY};
    double[] zs = {box.minZ, box.maxZ};
    for (double x : xs) {
      for (double y : ys) {
        for (double z : zs) {
          double sx = x == box.minX ? CORNER_MARK : -CORNER_MARK;
          double sy = y == box.minY ? CORNER_MARK : -CORNER_MARK;
          double sz = z == box.minZ ? CORNER_MARK : -CORNER_MARK;
          segment(pose, buffer, x, y, z, x + sx, y, z);
          segment(pose, buffer, x, y, z, x, y + sy, z);
          segment(pose, buffer, x, y, z, x, y, z + sz);
        }
      }
    }
  }

  private static void segment(
      PoseStack.Pose pose, VertexConsumer buffer,
      double x1, double y1, double z1, double x2, double y2, double z2) {
    buffer.addVertex(pose, (float) x1, (float) y1, (float) z1).setColor(1F, 1F, 1F, ALPHA)
        .setNormal(pose, 0F, 1F, 0F).setLineWidth(2F);
    buffer.addVertex(pose, (float) x2, (float) y2, (float) z2).setColor(1F, 1F, 1F, ALPHA)
        .setNormal(pose, 0F, 1F, 0F).setLineWidth(2F);
  }

  private static void renderLineBox(
      PoseStack.Pose poseStack, VertexConsumer buffer, AABB box,
      float red, float green, float blue, float alpha) {
    renderLineBox(poseStack, buffer,
        box.minX + 0.002, box.minY + 0.002, box.minZ + 0.002,
        box.maxX + 0.002, box.maxY + 0.002, box.maxZ + 0.002,
        red, green, blue, alpha, red, green, blue);
  }

  private static void renderLineBox(PoseStack.Pose posestack$pose, VertexConsumer consumer,
                                    double minX, double minY, double minZ,
                                    double maxX, double maxY, double maxZ,
                                    float red, float green, float blue, float alpha,
                                    float red2, float green2, float blue2
  ) {
    float f = (float) minX;
    float f1 = (float) minY;
    float f2 = (float) minZ;
    float f3 = (float) maxX;
    float f4 = (float) maxY;
    float f5 = (float) maxZ;
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
