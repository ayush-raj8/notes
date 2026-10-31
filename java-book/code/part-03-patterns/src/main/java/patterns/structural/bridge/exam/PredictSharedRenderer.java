package patterns.structural.bridge.exam;

import patterns.structural.bridge.CircleShape;
import patterns.structural.bridge.RectShape;
import patterns.structural.bridge.Renderer;
import patterns.structural.bridge.Shape;

// ANCHOR: predict-shared-renderer
public final class PredictSharedRenderer {
  static final class Counting implements Renderer {
    int calls;

    @Override
    public String circle(int radius) {
      calls++;
      return "c";
    }

    @Override
    public String rect(int width, int height) {
      calls++;
      return "r";
    }
  }

  public static void main(String[] args) {
    Counting counting = new Counting();
    Shape[] shapes = {new CircleShape(counting, 1), new RectShape(counting, 1, 1), new CircleShape(counting, 2)};
    StringBuilder sb = new StringBuilder();
    for (Shape s : shapes) {
      sb.append(s.draw());
    }
    System.out.println(sb);
    System.out.println(counting.calls);
  }
}
// ANCHOR_END: predict-shared-renderer
