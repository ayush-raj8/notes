package oop.objectc.exam;

// ANCHOR: predict-integer-cache
public final class PredictIntegerCache {
  public static void main(String[] args) {
    Integer a = 100;
    Integer b = 100;
    Integer c = 1000;
    Integer d = 1000;
    System.out.println((a == b) + " " + (c == d) + " " + c.equals(d));
  }
}
// ANCHOR_END: predict-integer-cache
