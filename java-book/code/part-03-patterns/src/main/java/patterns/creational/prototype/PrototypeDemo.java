package patterns.creational.prototype;

// ANCHOR: prototype-demo
public final class PrototypeDemo {
  public static void main(String[] args) {
    ShapeRegistry registry = new ShapeRegistry();
    registry.register("small-circle", new Circle("base", 1));
    registry.register("box", new Rect("panel", 4, 2));

    Shape a = registry.create("small-circle");
    Shape b = registry.create("small-circle");
    ((Circle) a).grow(10);
    a.relabel("big");
    System.out.println(a);
    System.out.println(b);
    System.out.println(a == b);
    System.out.println(registry.create("box"));
    try {
      registry.create("hexagon");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: prototype-demo
