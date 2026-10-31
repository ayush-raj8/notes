package patterns.structural.bridge;

// ANCHOR: rect-shape
public final class RectShape extends Shape {
  private final int width;
  private final int height;

  public RectShape(Renderer renderer, int width, int height) {
    super(renderer);
    this.width = width;
    this.height = height;
  }

  @Override
  public String draw() {
    return renderer.rect(width, height);
  }
}
// ANCHOR_END: rect-shape
