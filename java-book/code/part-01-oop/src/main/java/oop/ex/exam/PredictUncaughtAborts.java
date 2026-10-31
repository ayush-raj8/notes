package oop.ex.exam;

// ANCHOR: predict-uncaught-aborts
public final class PredictUncaughtAborts {
  static int divide(int a, int b) {
    return a / b;
  }

  public static void main(String[] args) {
    System.out.println("start");
    try {
      System.out.println(divide(6, 3));
      System.out.println(divide(1, 0));
      System.out.println("not printed");
    } finally {
      System.out.println("finally");
    }
    System.out.println("after");
  }
}
// ANCHOR_END: predict-uncaught-aborts
