package patterns.structural.bridge;

// ANCHOR: bridge-demo
public final class BridgeDemo {
  public static void main(String[] args) {
    Renderer vector = new VectorRenderer();
    Renderer raster = new RasterRenderer();
    Shape[] shapes = {
      new CircleShape(vector, 5),
      new CircleShape(raster, 5),
      new RectShape(vector, 2, 3),
      new RectShape(raster, 2, 3)
    };
    for (Shape s : shapes) {
      System.out.println(s.draw());
    }
    System.out.println(shapes.length + " combinations from 2 shapes + 2 renderers");
  }
}
// ANCHOR_END: bridge-demo
