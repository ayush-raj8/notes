package patterns.creational.prototype;

// ANCHOR: circle
public final class Circle extends Shape {
  private int radius;

  public Circle(String label, int radius) {
    super(label);
    this.radius = radius;
  }

  private Circle(Circle other) {
    super(other);
    this.radius = other.radius;
  }

  @Override
  public Circle copy() { // covariant return
    return new Circle(this);
  }

  public void grow(int by) {
    radius += by;
  }

  @Override
  public String toString() {
    return "Circle(" + label() + ", r=" + radius + ")";
  }
}
// ANCHOR_END: circle
