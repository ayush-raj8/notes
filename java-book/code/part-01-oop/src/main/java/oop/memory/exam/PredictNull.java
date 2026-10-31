package oop.memory.exam;

import oop.memory.Point;

// ANCHOR: predict-null
public final class PredictNull {
  public static void main(String[] args) {
    Point p = null;
    System.out.println(p == null);
    try {
      System.out.println(p.x());
    } catch (NullPointerException e) {
      System.out.println("caught");
    }
  }
}
// ANCHOR_END: predict-null
