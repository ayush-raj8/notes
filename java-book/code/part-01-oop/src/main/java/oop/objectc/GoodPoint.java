package oop.objectc;

import java.util.Objects;

// ANCHOR: good-point
/** Value class that honours the equals/hashCode/toString contract. */
public final class GoodPoint {
  private final int x;
  private final int y;

  public GoodPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GoodPoint p)) {
      return false;
    }
    return x == p.x && y == p.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "GoodPoint[x=" + x + ", y=" + y + "]";
  }
}
// ANCHOR_END: good-point
