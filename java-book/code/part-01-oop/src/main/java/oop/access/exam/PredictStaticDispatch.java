package oop.access.exam;

// ANCHOR: predict-static-dispatch
public final class PredictStaticDispatch {
  static class Parent {
    static String who() {
      return "Parent";
    }

    String me() {
      return "Parent";
    }
  }

  static class Child extends Parent {
    static String who() {
      return "Child";
    }

    @Override
    String me() {
      return "Child";
    }
  }

  public static void main(String[] args) {
    Parent p = new Child();
    System.out.println(p.who());
    System.out.println(p.me());
  }
}
// ANCHOR_END: predict-static-dispatch
