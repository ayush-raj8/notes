package oop.override.solutions;

/** Exercise 1: covariant copy() so callers need no cast. */
public final class SolutionCovariantCopy {
  static class Shape {
    Shape copy() {
      return new Shape();
    }
  }

  static class Circle extends Shape {
    final int r;

    Circle(int r) {
      this.r = r;
    }

    @Override
    Circle copy() {
      return new Circle(r);
    }
  }

  public static void main(String[] args) {
    Circle c = new Circle(4);
    Circle d = c.copy();
    System.out.println(d.r + " " + (c != d));
  }
}
