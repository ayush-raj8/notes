package solutions.ch48;

/** Exercise 1: area and perimeter as two visitors over Circle and Rect. */
public final class SolutionShapeVisitor {
  interface Visitor<R> {
    R circle(Circle c);

    R rect(Rect r);
  }

  interface Shape {
    <R> R accept(Visitor<R> v);
  }

  record Circle(double r) implements Shape {
    public <R> R accept(Visitor<R> v) {
      return v.circle(this);
    }
  }

  record Rect(double w, double h) implements Shape {
    public <R> R accept(Visitor<R> v) {
      return v.rect(this);
    }
  }

  static final class Area implements Visitor<Double> {
    public Double circle(Circle c) {
      return Math.PI * c.r() * c.r();
    }

    public Double rect(Rect r) {
      return r.w() * r.h();
    }
  }

  static final class Perimeter implements Visitor<Double> {
    public Double circle(Circle c) {
      return 2 * Math.PI * c.r();
    }

    public Double rect(Rect r) {
      return 2 * (r.w() + r.h());
    }
  }

  public static void main(String[] args) {
    for (Shape s : new Shape[] {new Circle(1), new Rect(2, 3)}) {
      System.out.printf("%s area=%.2f perimeter=%.2f%n", s, s.accept(new Area()), s.accept(new Perimeter()));
    }
  }
}
