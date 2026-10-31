package oop.ex.exam;

// ANCHOR: predict-nested-order
public final class PredictNestedOrder {
  static void f() {
    try {
      try {
        System.out.println("1");
        throw new RuntimeException("a");
      } catch (RuntimeException e) {
        System.out.println("2");
        throw new IllegalStateException("b");
      } finally {
        System.out.println("3");
      }
    } catch (IllegalStateException e) {
      System.out.println("4 " + e.getMessage());
    } finally {
      System.out.println("5");
    }
    System.out.println("6");
  }

  public static void main(String[] args) {
    f();
  }
}
// ANCHOR_END: predict-nested-order
