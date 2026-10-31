package oop.compare.exam;

// ANCHOR: predict-ref-type-overload
class P1A {
  String m(Object o) {
    return "A.Object";
  }
}

class P1B extends P1A {
  String m(String s) {
    return "B.String";
  }

  @Override
  String m(Object o) {
    return "B.Object";
  }
}

public final class PredictRefTypeOverload {
  public static void main(String[] args) {
    P1A a = new P1B();
    P1B b = new P1B();
    System.out.println(a.m("x"));
    System.out.println(b.m("x"));
    System.out.println(b.m((Object) "x"));
    System.out.println(a.m(1));
  }
}
// ANCHOR_END: predict-ref-type-overload
