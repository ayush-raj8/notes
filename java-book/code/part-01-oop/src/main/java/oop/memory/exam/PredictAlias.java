package oop.memory.exam;

import oop.memory.Point;

// ANCHOR: predict-alias
public final class PredictAlias {
  public static void main(String[] args) {
    Point a = new Point(1, 1);
    Point b = a;
    Point c = new Point(1, 1);
    a.moveBy(0, 1);
    System.out.println(a);
    System.out.println(b);
    System.out.println(c);
    System.out.println(a == b);
    System.out.println(a == c);
  }
}
// ANCHOR_END: predict-alias
