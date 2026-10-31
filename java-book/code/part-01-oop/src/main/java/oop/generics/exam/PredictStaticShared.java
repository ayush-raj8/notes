package oop.generics.exam;

// ANCHOR: predict-static-shared
public final class PredictStaticShared {
  static final class Counter<T> {
    static int n;

    Counter() {
      n++;
    }
  }

  public static void main(String[] args) {
    new Counter<String>();
    new Counter<Integer>();
    System.out.println(Counter.n);
  }
}
// ANCHOR_END: predict-static-shared
