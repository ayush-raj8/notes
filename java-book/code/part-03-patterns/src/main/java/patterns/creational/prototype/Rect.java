package patterns.creational.prototype;

// ANCHOR: rect
public final class Rect extends Shape {
  private final int width;
  private final int height;

  public Rect(String label, int width, int height) {
    super(label);
    this.width = width;
    this.height = height;
  }

  private Rect(Rect other) {
    super(other);
    this.width = other.width;
    this.height = other.height;
  }

  @Override
  public Rect copy() {
    return new Rect(this);
  }

  @Override
  public String toString() {
    return "Rect(" + label() + ", " + width + "x" + height + ")";
  }
}
// ANCHOR_END: rect
