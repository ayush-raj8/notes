package oop.ex.exam;

// ANCHOR: predict-nested-try
public final class PredictNestedTry {
  public static void main(String[] args) {
    try {
      try {
        System.out.println("1");
        throw new IllegalStateException("x");
      } catch (IllegalArgumentException e) {
        System.out.println("2");
      } finally {
        System.out.println("3");
      }
      System.out.println("4");
    } catch (RuntimeException e) {
      System.out.println("5 " + e.getMessage());
    } finally {
      System.out.println("6");
    }
    System.out.println("7");
  }
}
// ANCHOR_END: predict-nested-try
