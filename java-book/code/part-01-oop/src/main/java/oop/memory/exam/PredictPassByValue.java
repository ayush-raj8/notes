package oop.memory.exam;

import oop.memory.Point;

// ANCHOR: predict-pass-by-value
public final class PredictPassByValue {
  static void touch(Point q) {
    q.moveBy(1, 1);
    q = new Point(0, 0);
  }

  public static void main(String[] args) {
    Point p = new Point(2, 2);
    touch(p);
    System.out.println(p);
  }
}
// ANCHOR_END: predict-pass-by-value
