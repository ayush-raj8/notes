package oop.memory;

// ANCHOR: point-class
/** Baseline mutable point: one object on the heap, references on the stack. */
public final class Point {
  private int x;
  private int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public void moveBy(int dx, int dy) {
    this.x += dx;
    this.y += dy;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
// ANCHOR_END: point-class
