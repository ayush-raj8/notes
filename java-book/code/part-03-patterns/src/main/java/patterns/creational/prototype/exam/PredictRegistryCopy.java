package patterns.creational.prototype.exam;

import patterns.creational.prototype.Circle;
import patterns.creational.prototype.Shape;
import patterns.creational.prototype.ShapeRegistry;

// ANCHOR: predict-registry-copy
public final class PredictRegistryCopy {
  public static void main(String[] args) {
    ShapeRegistry r = new ShapeRegistry();
    r.register("c", new Circle("base", 5));
    Shape first = r.create("c");
    first.relabel("changed");
    ((Circle) first).grow(1);
    Shape second = r.create("c");
    System.out.println(first);
    System.out.println(second);
    System.out.println(first.getClass() == second.getClass());
  }
}
// ANCHOR_END: predict-registry-copy
