package patterns.structural.bridge;

// ANCHOR: renderer
/** Implementor: the low-level drawing API, independent of the shape hierarchy. */
public interface Renderer {
  String circle(int radius);

  String rect(int width, int height);
}
// ANCHOR_END: renderer
