package oop.poly.exam;

// ANCHOR: predict-overload-dispatch
class Q1A {
  String f(Object o) {
    return "A.Object";
  }

  String f(String s) {
    return "A.String";
  }
}

class Q1B extends Q1A {
  @Override
  String f(Object o) {
    return "B.Object";
  }
}

public final class PredictOverloadDispatch {
  public static void main(String[] args) {
    Q1A a = new Q1B();
    Object s = "x";
    System.out.println(a.f("x"));
    System.out.println(a.f(s));
    System.out.println(a.f(null));
  }
}
// ANCHOR_END: predict-overload-dispatch
