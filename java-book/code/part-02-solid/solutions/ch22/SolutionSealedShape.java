package solid.ocp.solutions;

/** Exercise 4: a closed set of shapes with an exhaustive switch and no default. */
public final class SolutionSealedShape {
  sealed interface Shape {
    record Circle(double r) implements Shape {}

    record Rect(double w, double h) implements Shape {}
  }

  static double area(Shape s) {
    return switch (s) {
      case Shape.Circle c -> Math.round(Math.PI * c.r() * c.r() * 100) / 100.0;
      case Shape.Rect r -> r.w() * r.h();
    };
  }

  public static void main(String[] args) {
    System.out.println(area(new Shape.Circle(1)));
    System.out.println(area(new Shape.Rect(2, 3)));
  }
}
