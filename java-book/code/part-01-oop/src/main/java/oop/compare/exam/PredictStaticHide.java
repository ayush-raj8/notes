package oop.compare.exam;

// ANCHOR: predict-static-hide
class P2A {
  static String s() {
    return "A.s";
  }

  String i() {
    return s();
  }
}

class P2B extends P2A {
  static String s() {
    return "B.s";
  }
}

public final class PredictStaticHide {
  public static void main(String[] args) {
    P2A x = new P2B();
    P2A n = null;
    System.out.println(x.s());
    System.out.println(x.i());
    System.out.println(new P2B().i());
    System.out.println(P2B.s());
    System.out.println(n.s());
  }
}
// ANCHOR_END: predict-static-hide
