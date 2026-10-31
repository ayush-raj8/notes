package patterns.creational.prototype.exam;

import patterns.creational.prototype.Circle;
import patterns.creational.prototype.Shape;

// ANCHOR: predict-covariant-copy
public final class PredictCovariantCopy {
  public static void main(String[] args) {
    Circle c = new Circle("a", 2);
    Circle typed = c.copy(); // no cast: covariant return
    Shape asShape = c;
    Shape viaBase = asShape.copy(); // dispatches to Circle.copy()
    System.out.println(typed);
    System.out.println(viaBase.getClass().getSimpleName());
    System.out.println(viaBase instanceof Circle);
  }
}
// ANCHOR_END: predict-covariant-copy
