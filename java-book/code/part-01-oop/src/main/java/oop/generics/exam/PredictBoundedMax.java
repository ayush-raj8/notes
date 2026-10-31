package oop.generics.exam;

// ANCHOR: predict-bounded-max
public final class PredictBoundedMax {
  static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;
  }

  public static void main(String[] args) {
    System.out.println(max("pear", "apple") + " " + max(10, 9) + " " + max("b", "B"));
  }
}
// ANCHOR_END: predict-bounded-max
