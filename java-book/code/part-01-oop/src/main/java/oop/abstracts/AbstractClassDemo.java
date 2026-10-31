package oop.abstracts;

// ANCHOR: abstract-class-demo
abstract class Shape {
  private final String name;

  Shape(String name) {
    this.name = name;
    System.out.println("Shape ctor " + name);
  }

  abstract double area();

  String report() {
    return name + " area=" + area();
  }
}

class Square extends Shape {
  private final double side;

  Square(double side) {
    super("square");
    this.side = side;
  }

  @Override
  double area() {
    return side * side;
  }
}

/** An abstract class has state, constructors and concrete methods; it cannot be instantiated. */
public final class AbstractClassDemo {
  public static void main(String[] args) {
    Shape s = new Square(3);
    System.out.println(s.report());
    Shape anon = new Shape("unit") {
      @Override
      double area() {
        return 1;
      }
    };
    System.out.println(anon.report());
    System.out.println(anon.getClass().getSuperclass().getSimpleName());
  }
}
// ANCHOR_END: abstract-class-demo
