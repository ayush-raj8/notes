package oop.nested.exam;

// ANCHOR: predict-anon-init
public final class PredictAnonInit {
  static class Base {
    Base() {
      System.out.println("Base ctor");
      hook();
    }

    void hook() {
      System.out.println("Base hook");
    }
  }

  public static void main(String[] args) {
    new Base() {
      int n = 5;

      {
        System.out.println("init block n=" + n);
      }

      @Override
      void hook() {
        System.out.println("anon hook n=" + n);
      }
    };
  }
}
// ANCHOR_END: predict-anon-init
