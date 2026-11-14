package es.degrassi.mmr.client.integration.athena.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

public record MMRAthenaQuad(int sprite, float left, float right, float top, float bottom, Rotation rotation,
                            float depth, boolean cull, int tint) {
  public static MMRAthenaQuad withSprite(int sprite, int tint) {
    return withSprite(sprite, 0f, tint);
  }

  public static MMRAthenaQuad withSprite(int sprite, float depth, int tint) {
    return withRotation(sprite, Rotation.NONE, depth, tint);
  }

  /**
   * A quad of a part of a face: {@code left}/{@code right} and {@code top}/{@code bottom}
   * are the corners of the part in the 0..1 space of the face.
   */
  public static MMRAthenaQuad withSprite(int sprite, float left, float right, float top, float bottom,
                                         float depth, int tint) {
    return new MMRAthenaQuad(sprite, left, right, top, bottom, Rotation.NONE, depth, true, tint);
  }

  /**
   * The four quadrants of a face, exactly like the quadrants the casing is drawn in.
   *
   * <p>Drawing a layer with the same geometry as the layer below it is what keeps them
   * from fighting for the depth: both are interpolated the same way, so their depths are
   * the same and the layer drawn last always wins the depth test. Moving the layer
   * forward instead fights the depth buffer from a certain distance, and shows a seam
   * along the edges of the overlay.
   */
  public static List<MMRAthenaQuad> quadrants(int sprite, float depth, int tint) {
    return List.of(
        withSprite(sprite, 0f, 0.5f, 1f, 0.5f, depth, tint),
        withSprite(sprite, 0.5f, 1f, 1f, 0.5f, depth, tint),
        withSprite(sprite, 0f, 0.5f, 0.5f, 0f, depth, tint),
        withSprite(sprite, 0.5f, 1f, 0.5f, 0f, depth, tint)
    );
  }

  public static MMRAthenaQuad withRotation(int sprite, Rotation rotation, int tint) {
    return withRotation(sprite, rotation, 0f, tint);
  }

  public static MMRAthenaQuad withRotation(int sprite, Rotation rotation, float depth, int tint) {
    return new MMRAthenaQuad(sprite, 0.0F, 1.0F, 1.0F, 0.0F, rotation, depth, true, tint);
  }

  public static MMRAthenaQuad withState(MMRMaterialMap map, Direction direction, boolean first, boolean second, boolean firstSecond, float left, float right, float top, float bottom) {
    int tex = CtmUtils.getTexture(first, second, firstSecond);
    int texture = map.getTexture(direction, tex);
    return new MMRAthenaQuad(texture, left, right, top, bottom, Rotation.NONE, 0.0F, true, -1);
  }

  public static MMRAthenaQuad withState(boolean first, boolean second, boolean firstSecond, float left, float right, float top, float bottom) {
    int texture = CtmUtils.getTexture(first, second, firstSecond);
    return new MMRAthenaQuad(texture, left, right, top, bottom, Rotation.NONE, 0.0F, true, -1);
  }

  public static MMRAthenaQuad withState(boolean first, boolean second, boolean firstSecond, float left, float right, float top, float bottom, float depth) {
    int texture = CtmUtils.getTexture(first, second, firstSecond);
    return new MMRAthenaQuad(texture, left, right, top, bottom, Rotation.NONE, depth, depth == 0f, -1);
  }
}
