package patterns.structural.bridge.exam;

import patterns.structural.bridge.CircleShape;
import patterns.structural.bridge.Renderer;

// ANCHOR: compile-ok-anonymous-renderer
/** Compiles: an anonymous class may implement both methods; a lambda could not (two abstract methods). */
public final class CompileOkAnonymousRenderer {
  public static void main(String[] args) {
    Renderer ascii =
        new Renderer() {
          @Override
          public String circle(int radius) {
            return "o".repeat(radius);
          }

          @Override
          public String rect(int width, int height) {
            return "#".repeat(width);
          }
        };
    System.out.println(new CircleShape(ascii, 3).draw());
  }
}
// ANCHOR_END: compile-ok-anonymous-renderer
