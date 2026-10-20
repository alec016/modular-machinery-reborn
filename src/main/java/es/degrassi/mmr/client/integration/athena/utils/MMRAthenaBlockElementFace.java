package es.degrassi.mmr.client.integration.athena.utils;

import earth.terrarium.athena.api.client.models.TintProvider;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import org.joml.Vector3f;

/**
 * Re-implementation of the 1.21.1 {@code MMRAthenaBlockElementFace} on top of the 26.1 cuboid
 * baking primitives ({@code CuboidFace}/{@code BakedQuad.MaterialInfo}).
 *
 * <p>The old class built a vanilla {@code BlockElementFace} + {@code BlockFaceUV} which the
 * vanilla {@code FaceBakery} then baked. Those classes no longer exist, but the pieces this
 * helper provides are exactly what the ported {@code CtmUtils#bakeQuad} needs to replicate the
 * vanilla element bake one-to-one: the pre-rotated UV rect (model space 0..16), the per-face
 * tint index and the static tint colour.
 */
public class MMRAthenaBlockElementFace {

  public static MMRAthenaBlockElementFace.Result of(MMRAthenaQuad quad, Direction direction, Vector3f start, Vector3f end,
                                    TintProvider tint) {
    int tintIndex = -1;
    ExtraFaceData extraData = null;

    switch (tint) {
      case TintProvider.Index(var index) -> tintIndex = index;
      case TintProvider.Static(var color) -> extraData = new ExtraFaceData(
          color,
          ExtraFaceData.DEFAULT.lightEmission(),
          ExtraFaceData.DEFAULT.ambientOcclusion()
      );
      case null -> {}
    }

    return new Result(direction, tintIndex, getUVs(start, end, direction), extraData);
  }

  public static float[] getUVs(Vector3f from, Vector3f to, Direction direction) {
    float[] uvs = switch (direction) {
      case UP -> new float[] { from.x(), to.z(), to.x(), from.z() };
      case DOWN -> new float[]{ from.x(), 16 - from.z(), to.x(), 16 - to.z() };
      case NORTH -> new float[] { 16 - from.x, 16 - to.y, 16 - to.x, 16 - from.y };
      case SOUTH -> new float[]{ to.x, 16 - to.y, from.x, 16 - from.y };
      case WEST -> new float[]{ to.z(), 16.0F - to.y(), from.z(), 16.0F - from.y() };
      case EAST -> new float[]{16.0F - from.z, 16.0F - to.y(), 16.0F - to.z, 16.0F - from.y()};
    };
    return new float[] { uvs[0], uvs[1], uvs[2], uvs[3] };
  }

  public record Result(Direction direction, int tintIndex, float[] uvs, ExtraFaceData extraData) {}
}
