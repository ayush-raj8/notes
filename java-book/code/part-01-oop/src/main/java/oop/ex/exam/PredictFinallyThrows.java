package oop.ex.exam;

// ANCHOR: predict-finally-throws
public final class PredictFinallyThrows {
  static void m() {
    try {
      throw new RuntimeException("from try");
    } finally {
      throw new IllegalStateException("from finally");
    }
  }

  public static void main(String[] args) {
    try {
      m();
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getSuppressed().length);
    }
  }
}
// ANCHOR_END: predict-finally-throws
