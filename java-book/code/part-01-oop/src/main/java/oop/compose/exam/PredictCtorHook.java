package oop.compose.exam;

// ANCHOR: predict-ctor-hook
public final class PredictCtorHook {
  static class Base {
    Base() {
      log();
    }

    void log() {
      System.out.println("base");
    }
  }

  static class Sub extends Base {
    private String tag = "T";

    @Override
    void log() {
      System.out.println("tag=" + tag);
    }
  }

  public static void main(String[] args) {
    new Sub();
  }
}
// ANCHOR_END: predict-ctor-hook
