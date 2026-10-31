package patterns.structural.bridge.exam;

import patterns.structural.bridge.CircleShape;
import patterns.structural.bridge.RasterRenderer;
import patterns.structural.bridge.RectShape;
import patterns.structural.bridge.Shape;
import patterns.structural.bridge.VectorRenderer;

// ANCHOR: predict-bridge-combos
public final class PredictBridgeCombos {
  public static void main(String[] args) {
    Shape a = new CircleShape(new VectorRenderer(), 2);
    Shape b = new RectShape(new RasterRenderer(), 4, 1);
    System.out.println(a.draw());
    System.out.println(b.draw());
    System.out.println(a.getClass().getSimpleName() + " " + b.getClass().getSuperclass().getSimpleName());
  }
}
// ANCHOR_END: predict-bridge-combos
