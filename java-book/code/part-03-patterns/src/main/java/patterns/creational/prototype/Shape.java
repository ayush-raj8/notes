package patterns.creational.prototype;

// ANCHOR: shape
/** Prototype: every shape can produce an independent copy of itself. */
public abstract class Shape {
  private String label;

  protected Shape(String label) {
    this.label = label;
  }

  /** Copy constructor used by subclasses. */
  protected Shape(Shape other) {
    this.label = other.label;
  }

  public abstract Shape copy();

  public String label() {
    return label;
  }

  public void relabel(String newLabel) {
    this.label = newLabel;
  }
}
// ANCHOR_END: shape
