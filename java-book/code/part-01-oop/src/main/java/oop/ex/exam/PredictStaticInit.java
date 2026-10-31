package oop.ex.exam;

// ANCHOR: predict-static-init
class Holder7 {
  static int v = compute();

  static int compute() {
    System.out.println("compute");
    return Integer.parseInt("seven");
  }
}

public final class PredictStaticInit {
  public static void main(String[] args) {
    for (int i = 0; i < 2; i++) {
      try {
        System.out.println(Holder7.v);
      } catch (Throwable t) {
        System.out.println(t.getClass().getSimpleName());
      }
    }
  }
}
// ANCHOR_END: predict-static-init
