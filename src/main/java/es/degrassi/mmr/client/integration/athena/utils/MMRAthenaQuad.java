package es.degrassi.mmr.client.integration.athena.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public record MMRAthenaQuad(int sprite, float left, float right, float top, float bottom, Rotation rotation,
                            float depth, boolean cull, int tint) {
  public static MMRAthenaQuad withSprite(int sprite, int tint) {
    return withSprite(sprite, 0f, tint);
  }

  public static MMRAthenaQuad withSprite(int sprite, float depth, int tint) {
    return withRotation(sprite, Rotation.NONE, depth, tint);
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
