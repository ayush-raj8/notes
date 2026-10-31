package oop.overload.exam;

// ANCHOR: predict-inherit
class A {
  String f(int n) {
    return "A";
  }
}

class B extends A {
  String f(String s) {
    return "B";
  }
}

public final class PredictInherit {
  public static void main(String[] args) {
    A a = new B();
    System.out.println(a.f(1));
  }
}
// ANCHOR_END: predict-inherit
