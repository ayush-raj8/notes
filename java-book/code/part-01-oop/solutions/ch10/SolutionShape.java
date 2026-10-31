package oop.abstracts.solutions;

/** Exercise 1: abstract Shape with a template method and two concrete subclasses. */
public final class SolutionShape {
  abstract static class Shape {
    abstract double area();

    final String report() { // template: subclasses cannot change the format
      return getClass().getSimpleName() + ":" + area();
    }
  }

  static final class Sq extends Shape {
    private final double s;

    Sq(double s) {
      this.s = s;
    }

    @Override
    double area() {
      return s * s;
    }
  }

  static final class Rc extends Shape {
    private final double w;
    private final double h;

    Rc(double w, double h) {
      this.w = w;
      this.h = h;
    }

    @Override
    double area() {
      return w * h;
    }
  }

  public static void main(String[] args) {
    System.out.println(new Sq(3).report());
    System.out.println(new Rc(2, 5).report());
  }
}
