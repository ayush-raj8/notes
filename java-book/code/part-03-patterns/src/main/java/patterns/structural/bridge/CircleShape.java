package patterns.structural.bridge;

// ANCHOR: circle-shape
/** Refined abstraction. */
public final class CircleShape extends Shape {
  private final int radius;

  public CircleShape(Renderer renderer, int radius) {
    super(renderer);
    this.radius = radius;
  }

  @Override
  public String draw() {
    return renderer.circle(radius);
  }
}
// ANCHOR_END: circle-shape
