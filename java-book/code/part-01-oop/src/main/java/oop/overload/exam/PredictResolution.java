package oop.overload.exam;

// ANCHOR: predict-resolution
public final class PredictResolution {
  static String m(long n) {
    return "long";
  }

  static String m(Integer n) {
    return "Integer";
  }

  public static void main(String[] args) {
    System.out.println(m(1));
  }
}
// ANCHOR_END: predict-resolution
