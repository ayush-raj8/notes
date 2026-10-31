package patterns.structural.bridge.exam;

import patterns.structural.bridge.Renderer;
import patterns.structural.bridge.Shape;
import patterns.structural.bridge.VectorRenderer;

// ANCHOR: predict-refined
public final class PredictRefined {
  static final class Framed extends Shape {
    private final Shape inner;

    Framed(Renderer renderer, Shape inner) {
      super(renderer);
      this.inner = inner;
    }

    @Override
    public String draw() {
      return "[" + inner.draw() + "] via " + renderer.getClass().getSimpleName();
    }
  }

  public static void main(String[] args) {
    Renderer v = new VectorRenderer();
    Shape plain =
        new Shape(v) {
          @Override
          public String draw() {
            return renderer.rect(1, 2);
          }
        };
    Shape framed = new Framed(v, plain);
    System.out.println(plain.draw());
    System.out.println(framed.draw());
  }
}
// ANCHOR_END: predict-refined
