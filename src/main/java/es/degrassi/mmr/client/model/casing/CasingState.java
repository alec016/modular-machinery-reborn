package es.degrassi.mmr.client.model.casing;

import net.minecraft.core.Direction;

import java.util.Arrays;
import java.util.Objects;

public final class CasingState {
  public static final CasingState DEFAULT;
  static {
    var masks = new int[6];
    Arrays.fill(masks, 0b1111);
    var adjacentCasingBlocks = new boolean[6];
    DEFAULT = new CasingState(masks, adjacentCasingBlocks);
  }

  private final int[] masks;
  private final boolean[] adjacentCasingBlocks;

  public CasingState(int[] masks, boolean[] adjacentCasingBlocks) {
    this.adjacentCasingBlocks = adjacentCasingBlocks.clone();
    this.masks = masks.clone();
  }

  public int getMask(Direction side) {
    return masks[side.get3DDataValue()];
  }

  public boolean hasAdjacentCasingBlock(Direction side) {
    return adjacentCasingBlocks[side.get3DDataValue()];
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CasingState that)) return false;
    return Arrays.equals(masks, that.masks) && Arrays.equals(adjacentCasingBlocks, that.adjacentCasingBlocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(masks), Arrays.hashCode(adjacentCasingBlocks));
  }
}
