package oop.ex.exam;

// ANCHOR: predict-rethrow
public final class PredictRethrow {
  static void run() {
    try {
      throw new IllegalArgumentException("first");
    } catch (IllegalArgumentException e) {
      System.out.println("catch " + e.getMessage());
      throw new IllegalStateException("second", e);
    } finally {
      System.out.println("finally");
    }
  }

  public static void main(String[] args) {
    try {
      run();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage() + " <- " + e.getCause().getMessage());
    }
  }
}
// ANCHOR_END: predict-rethrow
