package oop.ex.exam;

// ANCHOR: predict-ctor-throws
public final class PredictCtorThrows {
  static class A {
    A() {
      System.out.println("A");
      init();
    }

    void init() {}
  }

  static class B extends A {
    final String s = "set";

    B() {
      System.out.println("B");
      throw new RuntimeException("fail");
    }

    @Override
    void init() {
      System.out.println("init s=" + s);
    }
  }

  public static void main(String[] args) {
    B b = null;
    try {
      b = new B();
    } catch (RuntimeException e) {
      System.out.println("caught " + e.getMessage());
    }
    System.out.println(b);
  }
}
// ANCHOR_END: predict-ctor-throws
