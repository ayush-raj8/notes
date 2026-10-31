package patterns.structural.bridge;

// ANCHOR: bridge-edge-demo
public final class BridgeEdgeDemo {
  public static void main(String[] args) {
    try {
      new CircleShape(null, 1);
    } catch (NullPointerException e) {
      System.out.println("NPE: " + e.getMessage());
    }
    Renderer shared = new Renderer() {
      int calls;

      @Override
      public String circle(int radius) {
        return "call " + (++calls);
      }

      @Override
      public String rect(int width, int height) {
        return "call " + (++calls);
      }
    };
    Shape a = new CircleShape(shared, 1);
    Shape b = new RectShape(shared, 1, 1);
    System.out.println(a.draw() + ", " + b.draw() + ", " + a.draw());
  }
}
// ANCHOR_END: bridge-edge-demo
