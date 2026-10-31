package patterns.structural.bridge.exam;

import patterns.structural.bridge.CircleShape;
import patterns.structural.bridge.RectShape;
import patterns.structural.bridge.VectorRenderer;

// ANCHOR: predict-null-renderer
public final class PredictNullRenderer {
  public static void main(String[] args) {
    try {
      new RectShape(null, 1, 1);
      System.out.println("created");
    } catch (NullPointerException e) {
      System.out.println("failed: " + e.getMessage());
    }
    System.out.println(new CircleShape(new VectorRenderer(), 0).draw());
    System.out.println("done");
  }
}
// ANCHOR_END: predict-null-renderer
