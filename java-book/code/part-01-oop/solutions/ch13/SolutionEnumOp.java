package oop.immut.solutions;

/** Exercise 3: enum with an abstract method per constant. */
public final class SolutionEnumOp {
  enum Shape {
    CIRCLE {
      @Override
      double area(double d) {
        return Math.PI * d * d;
      }
    },
    SQUARE {
      @Override
      double area(double d) {
        return d * d;
      }
    };

    abstract double area(double d);
  }

  public static void main(String[] args) {
    System.out.println(Shape.SQUARE.area(3));
    System.out.printf("%.2f%n", Shape.CIRCLE.area(1));
  }
}
