package oop.ex.exam;

// ANCHOR: predict-finally-override
public final class PredictFinallyOverride {
  @SuppressWarnings("finally")
  static String g() {
    try {
      throw new RuntimeException("boom");
    } catch (RuntimeException e) {
      return "catch";
    } finally {
      return "finally";
    }
  }

  public static void main(String[] args) {
    System.out.println(g());
  }
}
// ANCHOR_END: predict-finally-override
