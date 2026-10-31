package oop.poly.exam;

import java.util.List;

// ANCHOR: predict-cast-flow
public final class PredictCastFlow {
  public static void main(String[] args) {
    try {
      System.out.println("1");
      Object o = List.of(1);
      String s = (String) o;
      System.out.println("2");
    } catch (ClassCastException e) {
      System.out.println("3");
    } finally {
      System.out.println("4");
    }
    System.out.println("5");
  }
}
// ANCHOR_END: predict-cast-flow
