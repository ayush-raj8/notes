package oop.poly.solutions;

import java.util.List;

/** Exercise 1: one loop, many runtime types. */
public final class SolutionShapeSum {
  interface Shape {
    double area();
  }

  record Sq(double s) implements Shape {
    public double area() {
      return s * s;
    }
  }

  record Rect(double w, double h) implements Shape {
    public double area() {
      return w * h;
    }
  }

  static double total(List<? extends Shape> shapes) {
    double sum = 0;
    for (Shape s : shapes) {
      sum += s.area(); // dispatched at run time
    }
    return sum;
  }

  public static void main(String[] args) {
    System.out.println(total(List.of(new Sq(2), new Rect(2, 3), new Sq(1))));
  }
}
