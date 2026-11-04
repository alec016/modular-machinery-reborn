package es.degrassi.mmr.client.screen.util;

import com.lowdragmc.lowdraglib2.client.scene.WorldSceneRenderer;
import com.lowdragmc.lowdraglib2.client.shader.LDLibRenderPipelines;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public final class MMRSceneFx {

  private static final RenderType FACE_TINT = RenderType.create(
      "mmr_face_tint",
      RenderSetup.builder(LDLibRenderPipelines.BLOCK_OVERLAY)
          .bufferSize(2048)
          .createRenderSetup()
  );

  private static final float FACE_PULL = 0.002f;
  private static final float FACE_INSET = 0.48f;
  private static final float GHOST_ALPHA = 0.5f;

  public static final float[] INPUT_TINT = {0f, 0.42f, 1f};
  public static final float[] OUTPUT_TINT = {1f, 0.48f, 0f};
  public static final float TINT_ALPHA = 0.5f;

  private MMRSceneFx() {}

  public static WorldSceneRenderer.VertexConsumerWrapper ghostBuffer(MultiBufferSource bufferSource) {
    var wrapper = new WorldSceneRenderer.VertexConsumerWrapper(
        bufferSource.getBuffer(RenderTypes.translucentMovingBlock()));
    wrapper.setColorMultiplier(1f, 1f, 1f, GHOST_ALPHA);
    return wrapper;
  }

  public static void tesselateGhost(ModelBlockRenderer renderer, BlockStateModelSet models,
                             BlockAndTintGetter region, VertexConsumer buffer,
                             BlockPos pos, BlockState state) {
    renderer.tesselateBlock(
        buffer::putBlockBakedQuad,
        pos.getX(), pos.getY(), pos.getZ(),
        region, pos, state, models.get(state), state.getSeed(pos));
  }

  public static void drawFaceTint(MultiBufferSource bufferSource, BlockPos pos, Direction face,
                           float r, float g, float b, float a) {
    VertexConsumer buffer = bufferSource.getBuffer(FACE_TINT);
    var pose = new PoseStack();
    pose.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    Matrix4f mat = pose.last().pose();
    float plane = 0.5f + FACE_PULL;
    float inset = FACE_INSET;
    switch (face) {
      case WEST -> quad(buffer, mat,
          -plane, -inset, -inset,
          -plane, -inset, inset,
          -plane, inset, inset,
          -plane, inset, -inset, r, g, b, a);
      case EAST -> quad(buffer, mat,
          plane, -inset, -inset,
          plane, inset, -inset,
          plane, inset, inset,
          plane, -inset, inset, r, g, b, a);
      case DOWN -> quad(buffer, mat,
          -inset, -plane, -inset,
          inset, -plane, -inset,
          inset, -plane, inset,
          -inset, -plane, inset, r, g, b, a);
      case UP -> quad(buffer, mat,
          -inset, plane, -inset,
          -inset, plane, inset,
          inset, plane, inset,
          inset, plane, -inset, r, g, b, a);
      case NORTH -> quad(buffer, mat,
          -inset, -inset, -plane,
          -inset, inset, -plane,
          inset, inset, -plane,
          inset, -inset, -plane, r, g, b, a);
      case SOUTH -> quad(buffer, mat,
          -inset, -inset, plane,
          -inset, inset, plane,
          inset, inset, plane,
          inset, -inset, plane, r, g, b, a);
    }
  }

  private static void quad(VertexConsumer buffer, Matrix4f mat,
                           float ax, float ay, float az,
                           float bx, float by, float bz,
                           float cx, float cy, float cz,
                           float dx, float dy, float dz,
                           float r, float g, float b, float a) {
    buffer.addVertex(mat, ax, ay, az).setColor(r, g, b, a);
    buffer.addVertex(mat, bx, by, bz).setColor(r, g, b, a);
    buffer.addVertex(mat, cx, cy, cz).setColor(r, g, b, a);
    buffer.addVertex(mat, dx, dy, dz).setColor(r, g, b, a);
  }
}
