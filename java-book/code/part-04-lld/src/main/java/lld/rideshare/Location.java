package lld.rideshare;

/** Grid point in whole kilometres; distance is Manhattan so fares stay in integers. */
public record Location(int x, int y) {
  public int distanceTo(Location other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }
}
