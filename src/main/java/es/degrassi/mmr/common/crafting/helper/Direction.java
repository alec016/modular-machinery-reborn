package es.degrassi.mmr.common.crafting.helper;

public enum Direction {
  TOP,
  LEFT,
  RIGHT,
  BOTTOM;

  public String toString() {
    return name().toLowerCase();
  }

  public boolean horizontal() {
    return this == LEFT || this == RIGHT;
  }

  public boolean endToStart() {
    return this == RIGHT || this == BOTTOM;
  }
}
