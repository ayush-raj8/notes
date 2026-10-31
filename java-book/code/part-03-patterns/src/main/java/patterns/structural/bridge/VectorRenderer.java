package patterns.structural.bridge;

// ANCHOR: vector-renderer
public final class VectorRenderer implements Renderer {
  @Override
  public String circle(int radius) {
    return "vector circle r=" + radius;
  }

  @Override
  public String rect(int width, int height) {
    return "vector rect " + width + "x" + height;
  }
}
// ANCHOR_END: vector-renderer
