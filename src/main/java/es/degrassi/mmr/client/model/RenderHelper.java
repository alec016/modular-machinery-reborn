package es.degrassi.mmr.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.List;

public class RenderHelper {

  private static EnumMap<Direction, List<Vector3f>> cornersForFacing = generateCornersForFacings();

  private RenderHelper() {

  }

  public static List<Vector3f> getFaceCorners(Direction side) {
    return cornersForFacing.get(side);
  }

  private static EnumMap<Direction, List<Vector3f>> generateCornersForFacings() {
    EnumMap<Direction, List<Vector3f>> result = new EnumMap<>(Direction.class);

    for (Direction facing : Direction.values()) {
      List<Vector3f> corners;

      float offset = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 0 : 1;

      corners = switch (facing.getAxis()) {
        case X -> Lists.newArrayList(new Vector3f(offset, 1, 1), new Vector3f(offset, 0, 1),
            new Vector3f(offset, 0, 0), new Vector3f(offset, 1, 0));
        case Y -> Lists.newArrayList(new Vector3f(1, offset, 1), new Vector3f(1, offset, 0),
            new Vector3f(0, offset, 0), new Vector3f(0, offset, 1));
        case Z -> Lists.newArrayList(new Vector3f(0, 1, offset), new Vector3f(0, 0, offset),
            new Vector3f(1, 0, offset), new Vector3f(1, 1, offset));
      };

      if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
        corners = Lists.reverse(corners);
      }

      result.put(facing, ImmutableList.copyOf(corners));
    }

    return result;
  }
}
