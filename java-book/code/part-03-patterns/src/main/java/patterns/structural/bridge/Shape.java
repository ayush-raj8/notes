package patterns.structural.bridge;

import java.util.Objects;

// ANCHOR: shape
/** Abstraction: holds a reference to the implementor and delegates the low-level work. */
public abstract class Shape {
  protected final Renderer renderer;

  protected Shape(Renderer renderer) {
    this.renderer = Objects.requireNonNull(renderer, "renderer");
  }

  public abstract String draw();
}
// ANCHOR_END: shape
