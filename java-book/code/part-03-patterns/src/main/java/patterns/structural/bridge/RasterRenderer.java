package patterns.structural.bridge;

// ANCHOR: raster-renderer
public final class RasterRenderer implements Renderer {
  @Override
  public String circle(int radius) {
    return "pixels for circle r=" + radius;
  }

  @Override
  public String rect(int width, int height) {
    return "pixels for rect " + width + "x" + height;
  }
}
// ANCHOR_END: raster-renderer
