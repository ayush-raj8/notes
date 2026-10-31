package patterns.structural.bridge.exam;

import patterns.structural.bridge.RasterRenderer;
import patterns.structural.bridge.Renderer;
import patterns.structural.bridge.VectorRenderer;

// ANCHOR: predict-runtime-swap
public final class PredictRuntimeSwap {
  static final class Canvas {
    private Renderer renderer;

    Canvas(Renderer renderer) {
      this.renderer = renderer;
    }

    void use(Renderer renderer) {
      this.renderer = renderer;
    }

    String circle() {
      return renderer.circle(1);
    }
  }

  public static void main(String[] args) {
    Canvas c = new Canvas(new VectorRenderer());
    System.out.println(c.circle());
    c.use(new RasterRenderer());
    System.out.println(c.circle());
    c.use(new VectorRenderer());
    System.out.println(c.circle());
  }
}
// ANCHOR_END: predict-runtime-swap
