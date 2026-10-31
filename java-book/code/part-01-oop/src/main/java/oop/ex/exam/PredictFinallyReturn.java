package oop.ex.exam;

// ANCHOR: predict-finally-return
public final class PredictFinallyReturn {
  static int f() {
    int x = 2;
    try {
      return x;
    } finally {
      x = 3;
      System.out.println("finally x=" + x);
    }
  }

  public static void main(String[] args) {
    System.out.println(f());
  }
}
// ANCHOR_END: predict-finally-return
