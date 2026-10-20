package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.client.scene.WorldSceneRenderer;
import com.lowdragmc.lowdraglib2.client.shader.LDLibRenderPipelines;
import com.lowdragmc.lowdraglib2.client.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

/**
 * World-space drawing helpers for the auto-IO {@link MMRTabs.SideScene}: colored face tints for
 * the auto-IO enabled sides and the translucent ghost render of the machine's surrounding blocks.
 *
 * <p>The tints reuse the very pipeline of LDLib2's own select overlay (see
 * {@link RenderUtils}: {@code position_color} quads through {@code BLOCK_OVERLAY}), so they pick
 * up the scene's camera and depth exactly like the green selection border does. The ghosts are
 * block models tessellated straight into the translucent moving-block buffer with a color
 * multiplier, which turns them into true 0.5-opacity renders.
 *
 * <p>Client-only: the helpers build client model mappings, so they are only ever touched from the
 * client side (the SideScene constructor guards it).
 */
final class MMRSceneFx {

  /**
   * Face tint pipeline: the LDLib2 select overlay's pipeline, wrapped into our own RenderType so
   * the buffer switch-heavy tooltip/meta draws never collide with its single shared usage.
   */
  private static final RenderType FACE_TINT = RenderType.create(
      "mmr_face_tint",
      RenderSetup.builder(LDLibRenderPipelines.BLOCK_OVERLAY)
          .bufferSize(2048)
          .createRenderSetup()
  );

  /** Pulls the quad out of the face plane so it never z-fights with the block surface. */
  private static final float FACE_PULL = 0.002f;
  /** Tightens the tint inside the face edges a touch, like a GT-style face pad. */
  private static final float FACE_INSET = 0.48f;
  /** Ghost opacity as requested: 0.5. */
  private static final float GHOST_ALPHA = 0.5f;

  /** Auto-IO enabled face tints: blue = auto input, orange = auto output (additive over the block). */
  static final float[] INPUT_TINT = {0f, 0.42f, 1f};
  static final float[] OUTPUT_TINT = {1f, 0.48f, 0f};
  static final float TINT_ALPHA = 0.5f;

  private MMRSceneFx() {}

  /**
   * A wrapped {@link RenderTypes#translucentMovingBlock()} consumer with the ghost color
   * multiplier applied: block quads pushed through it blend at {@link #GHOST_ALPHA}.
   */
  static WorldSceneRenderer.VertexConsumerWrapper ghostBuffer(MultiBufferSource bufferSource) {
    var wrapper = new WorldSceneRenderer.VertexConsumerWrapper(
        bufferSource.getBuffer(RenderTypes.translucentMovingBlock()));
    wrapper.setColorMultiplier(1f, 1f, 1f, GHOST_ALPHA);
    return wrapper;
  }

  /**
   * Tessellates one ghost block (a real-world {@code BlockState} at a dummy-world position) into
   * the ghost buffer. Same call pattern as the scene's own block compilation.
   */
  static void tesselateGhost(ModelBlockRenderer renderer, BlockStateModelSet models,
                             BlockAndTintGetter region, VertexConsumer buffer,
                             BlockPos pos, BlockState state) {
    renderer.tesselateBlock(
        buffer::putBlockBakedQuad,
        pos.getX(), pos.getY(), pos.getZ(),
        region, pos, state, models.get(state), state.getSeed(pos));
  }

  /**
   * One tinted face quad over the given block face, drawn slightly outside the block so normal
   * depth testing keeps it off hidden faces (the pipeline depth-tests like the select overlay).
   *
   * <p>Positions follow the exact recipe of the scene's own select overlay
   * ({@link RenderUtils#renderBlockOverLay}): translate to the block's center first and emit
   * center-relative corners, so these quads are glued to the same faces the overlay covers.
   */
  static void drawFaceTint(MultiBufferSource bufferSource, BlockPos pos, Direction face,
                           float r, float g, float b, float a) {
    VertexConsumer buffer = bufferSource.getBuffer(FACE_TINT);
    var pose = new PoseStack();
    pose.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    Matrix4f mat = pose.last().pose();
    float plane = 0.5f + FACE_PULL; // just outside the face plane, in center-relative space
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
