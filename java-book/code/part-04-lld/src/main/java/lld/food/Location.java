package lld.food;

public record Location(int x, int y) {
  /** Manhattan distance: city blocks, no square roots. */
  public int distanceTo(Location other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }
}
