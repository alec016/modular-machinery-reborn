package es.degrassi.mmr.api.client;

import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.Objects;

public final class Blitter {
  public static final RenderPipeline GUI_TEXTURED_OPAQUE = RenderPipelines.GUI_TEXTURED.toBuilder()
      .withLocation(ModularMachineryReborn.rl("pipeline/gui_textured_opaque"))
      .withColorTargetState(ColorTargetState.DEFAULT)
      .build();

  // This assumption is obviously bogus, but currently all textures are this size,
  // and it's impossible to get the texture size from an already loaded texture.
  // The coordinates will still be correct when a resource pack provides bigger textures as long
  // as each texture element is still positioned at the same relative position
  public static final int DEFAULT_TEXTURE_WIDTH = 256;
  public static final int DEFAULT_TEXTURE_HEIGHT = 256;

  private final Identifier texture;
  // This texture size is only used to convert the source rectangle into uv coordinates (which are [0,1] and work
  // with textures of any size at runtime).
  private final int referenceWidth;
  private final int referenceHeight;
  private int r = 255;
  private int g = 255;
  private int b = 255;
  private int a = 255;
  private Rect2i srcRect;
  private Rect2i destRect = new Rect2i(0, 0, 0, 0);
  private boolean blending = true;
  private TextureTransform transform = TextureTransform.NONE;
  private int zOffset;

  Blitter(Identifier texture, int referenceWidth, int referenceHeight) {
    this.texture = texture;
    this.referenceWidth = referenceWidth;
    this.referenceHeight = referenceHeight;
  }

  public static Blitter texture(Identifier file) {
    return texture(file, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT);
  }

  public static Blitter texture(String file) {
    return texture(file, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT);
  }

  public static Blitter texture(Identifier file, int referenceWidth, int referenceHeight) {
    return new Blitter(file, referenceWidth, referenceHeight);
  }

  public static Blitter texture(String file, int referenceWidth, int referenceHeight) {
    return new Blitter(ModularMachineryReborn.rl("textures/" + file), referenceWidth, referenceHeight);
  }

  public static Blitter sprite(TextureAtlasSprite sprite) {
    var atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());

    var padding = sprite.padding;
    return new Blitter(sprite.atlasLocation(), atlas.getWidth(), atlas.getHeight())
        .src(
            sprite.getX() + padding,
            sprite.getY() + padding,
            sprite.contents().width(),
            sprite.contents().height());
  }

  public static Blitter guiSprite(Identifier resourceLocation) {
    var sprites = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);
    var sprite = sprites.getSprite(resourceLocation);
    return sprite(sprite);
  }

  public static Blitter icon(Icon icon) {
    return Blitter.texture(icon.TEXTURE, icon.TEXTURE_WIDTH, icon.TEXTURE_HEIGHT)
        .src(icon.x, icon.y, icon.width, icon.height);
  }

  public Blitter copy() {
    Blitter result = new Blitter(texture, referenceWidth, referenceHeight);
    result.srcRect = srcRect;
    result.destRect = destRect;
    result.r = r;
    result.g = g;
    result.b = b;
    result.a = a;
    return result;
  }

  public int getSrcX() {
    return srcRect == null ? 0 : srcRect.getX();
  }

  public int getSrcY() {
    return srcRect == null ? 0 : srcRect.getY();
  }

  public int getSrcWidth() {
    return srcRect == null ? destRect.getWidth() : srcRect.getWidth();
  }

  public int getSrcHeight() {
    return srcRect == null ? destRect.getHeight() : srcRect.getHeight();
  }

  public Blitter src(int x, int y, int w, int h) {
    this.srcRect = new Rect2i(x, y, w, h);
    return this;
  }

  public Blitter srcWidth(int w) {
    this.srcRect = new Rect2i(srcRect.getX(), srcRect.getY(), w, srcRect.getHeight());
    return this;
  }

  public Blitter srcHeight(int h) {
    this.srcRect = new Rect2i(srcRect.getX(), srcRect.getY(), srcRect.getWidth(), h);
    return this;
  }

  public Blitter src(Rect2i rect) {
    return src(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
  }

  public Blitter dest(int x, int y, int w, int h) {
    this.destRect = new Rect2i(x, y, w, h);
    return this;
  }

  public Blitter dest(int x, int y) {
    return dest(x, y, 0, 0);
  }

  public Blitter dest(Rect2i rect) {
    return dest(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
  }

  public Rect2i getDestRect() {
    int x = destRect.getX();
    int y = destRect.getY();
    int w = 0, h = 0;
    if (destRect.getWidth() != 0 && destRect.getHeight() != 0) {
      w = destRect.getWidth();
      h = destRect.getHeight();
    } else if (srcRect != null) {
      w = srcRect.getWidth();
      h = srcRect.getHeight();
    }
    return new Rect2i(x, y, w, h);
  }

  public Blitter color(float r, float g, float b) {
    this.r = (int) (Mth.clamp(r, 0, 1) * 255);
    this.g = (int) (Mth.clamp(g, 0, 1) * 255);
    this.b = (int) (Mth.clamp(b, 0, 1) * 255);
    return this;
  }

  public Blitter colorArgb(int packedArgb) {
    this.a = ARGB.alpha(packedArgb);
    this.r = ARGB.red(packedArgb);
    this.g = ARGB.green(packedArgb);
    this.b = ARGB.blue(packedArgb);
    return this;
  }

  public Blitter opacity(float a) {
    this.a = (int) (Mth.clamp(a, 0, 1) * 255);
    return this;
  }

  public Blitter color(float r, float g, float b, float a) {
    return color(r, g, b).opacity(a);
  }

  public Blitter transform(TextureTransform transform) {
    this.transform = Objects.requireNonNull(transform);
    return this;
  }

  public Blitter blending(boolean enable) {
    this.blending = enable;
    return this;
  }

  public Blitter colorRgb(int packedRgb) {
    float r = (packedRgb >> 16 & 255) / 255.0F;
    float g = (packedRgb >> 8 & 255) / 255.0F;
    float b = (packedRgb & 255) / 255.0F;

    return color(r, g, b);
  }

  public Blitter zOffset(int offset) {
    this.zOffset = offset;
    return this;
  }

  public void blit(GuiGraphicsExtractor guiGraphics) {
    // With no source rectangle, we'll use the entirety of the texture. This happens rarely though.
    float minU, minV, maxU, maxV;
    if (srcRect == null) {
      minU = minV = 0;
      maxU = maxV = 1;
    } else {
      minU = srcRect.getX() / (float) referenceWidth;
      minV = srcRect.getY() / (float) referenceHeight;
      maxU = (srcRect.getX() + srcRect.getWidth()) / (float) referenceWidth;
      maxV = (srcRect.getY() + srcRect.getHeight()) / (float) referenceHeight;
    }

    // Transform the UV
    switch (transform) {
      case MIRROR_H -> {
        var tmp = minU;
        minU = maxU;
        maxU = tmp;
      }
      case MIRROR_V -> {
        var tmp = minV;
        minV = maxV;
        maxV = tmp;
      }
    }

    // It's possible to not set a destination rectangle size, in which case the
    // source rectangle size will be used
    float x1 = destRect.getX();
    float y1 = destRect.getY();
    float x2 = x1, y2 = y1;
    if (destRect.getWidth() != 0 && destRect.getHeight() != 0) {
      x2 += destRect.getWidth();
      y2 += destRect.getHeight();
    } else if (srcRect != null) {
      x2 += srcRect.getWidth();
      y2 += srcRect.getHeight();
    }

    var pipeline = blending ? RenderPipelines.GUI_TEXTURED : GUI_TEXTURED_OPAQUE;
    var texture = Minecraft.getInstance().getTextureManager().getTexture(this.texture);
    guiGraphics.submitGuiElementRenderState(new BlitRenderState(
        pipeline,
        TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
        new Matrix3x2f(guiGraphics.pose()),
        (int) x1, (int) y1,
        (int) x2, (int) y2,
        minU, maxU,
        minV, maxV,
        ARGB.color(a, r, g, b),
        guiGraphics.peekScissorStack()
    ));
  }

  public void blitWorld(
      PoseStack stack,
      SubmitNodeCollector collector,
      Direction face,
      int packedOverlay
  ) {
    RenderType renderType = blending
        ? RenderTypes.entityTranslucent(texture)
        : RenderTypes.entityCutout(texture);
    collector.submitCustomGeometry(stack, renderType, (pose, consumer) -> {
      float minU, minV, maxU, maxV;
      if (srcRect == null) {
        minU = minV = 0;
        maxU = maxV = 1;
      } else {
        minU = srcRect.getX() / (float) referenceWidth;
        minV = srcRect.getY() / (float) referenceHeight;
        maxU = (srcRect.getX() + srcRect.getWidth()) / (float) referenceWidth;
        maxV = (srcRect.getY() + srcRect.getHeight()) / (float) referenceHeight;
      }

      if (transform == TextureTransform.MIRROR_H) {
        float tmp = minU;
        minU = maxU;
        maxU = tmp;
      } else if (transform == TextureTransform.MIRROR_V) {
        float tmp = minV;
        minV = maxV;
        maxV = tmp;
      }

      float size = 12f / 16f;
      float margin = (1f - size) / 2f;
      float x2 = margin + size;
      float y2 = margin + size;

      switch (face) {
        case NORTH -> pose.translate(0, 0, -0.001f);
        case SOUTH -> pose.translate(0, 0, 0.001f);
        case WEST -> pose.translate(-0.001f, 0, 0);
        case EAST -> pose.translate(0.001f, 0, 0);
        case DOWN -> pose.translate(0, -0.001f, 0);
        case UP -> pose.translate(0, 0.001f, 0);
      }

      pose.translate(0.5f, 0.5f, 0.5f);

      Vec3i n = face.getUnitVec3i();
      float nx = n.getX();
      float ny = n.getY();
      float nz = n.getZ();

      switch (face) {
        case UP, DOWN -> {
          var player = Minecraft.getInstance().player;
          if (player == null) break;
          var playerFacing = player.getDirection();
          float snappedYaw = switch (playerFacing) {
            case NORTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f;
          };

          pose.mulPose(Axis.YP.rotationDegrees(-snappedYaw).get(new Matrix4f()));
        }
        default -> {}
      }

      pose.translate(-0.5f, -0.5f, -0.5f);

      switch (face) {
        case NORTH, SOUTH, EAST, WEST -> {
          float tmp = minV;
          minV = maxV;
          maxV = tmp;
        }
        case UP -> {
          float tempU = minU;
          minU = maxU;
          maxU = tempU;
          float tempV = minV;
          minV = maxV;
          maxV = tempV;
        }
        case DOWN -> {
          float tmp = minU;
          minU = maxU;
          maxU = tmp;
        }
      }

      emitFaceQuad(
          consumer, pose,
          face,
          margin, margin, x2, y2,
          minU, minV, maxU, maxV,
          nx, ny, nz,
          packedOverlay
      );
    });
  }

  private void emitFaceQuad(
      VertexConsumer vc,
      PoseStack.Pose mat,
      Direction face,
      float x1, float y1, float x2, float y2,
      float u1, float v1, float u2, float v2,
      float nx, float ny, float nz,
      int packedOverlay
  ) {
    switch (face) {
      case NORTH -> {
        vertex(vc, mat, x1, y1, 0, u2, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, y1, 0, u1, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, y2, 0, u1, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x1, y2, 0, u2, v2, nx, ny, nz, packedOverlay);
      }
      case SOUTH -> {
        vertex(vc, mat, x2, y1, 1, u2, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x1, y1, 1, u1, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x1, y2, 1, u1, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, y2, 1, u2, v2, nx, ny, nz, packedOverlay);
      }
      case WEST -> {
        vertex(vc, mat, 0, y1, x2, u2, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, 0, y1, x1, u1, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, 0, y2, x1, u1, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, 0, y2, x2, u2, v2, nx, ny, nz, packedOverlay);
      }
      case EAST -> {
        vertex(vc, mat, 1, y1, x1, u2, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, 1, y1, x2, u1, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, 1, y2, x2, u1, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, 1, y2, x1, u2, v2, nx, ny, nz, packedOverlay);
      }
      case UP -> {
        vertex(vc, mat, x1, 1, y2, u1, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, 1, y2, u2, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, 1, y1, u2, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x1, 1, y1, u1, v1, nx, ny, nz, packedOverlay);
      }
      case DOWN -> {
        vertex(vc, mat, x1, 0, y1, u1, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, 0, y1, u2, v1, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x2, 0, y2, u2, v2, nx, ny, nz, packedOverlay);
        vertex(vc, mat, x1, 0, y2, u1, v2, nx, ny, nz, packedOverlay);
      }
    }
  }

  private void vertex(
      VertexConsumer vc,
      PoseStack.Pose mat,
      float x, float y, float z,
      float u, float v,
      float nx, float ny, float nz,
      int packedOverlay
  ) {
    vc.addVertex(mat, x, y, z)
        .setColor(r, g, b, a)
        .setUv(u, v)
        .setOverlay(packedOverlay)
        .setLight(15728880)
        .setNormal(nx, ny, nz);
  }
}