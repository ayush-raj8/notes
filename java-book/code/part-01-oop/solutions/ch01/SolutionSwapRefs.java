package oop.memory.solutions;

import oop.memory.Point;

/** Shows why a naive swap of parameters cannot swap the caller's two variables. */
public final class SolutionSwapRefs {
  static void cannotSwap(Point a, Point b) {
    Point t = a;
    a = b;
    b = t;
  }

  public static void main(String[] args) {
    Point p = new Point(1, 0);
    Point q = new Point(2, 0);
    cannotSwap(p, q);
    System.out.println(p + " " + q);
  }
}
