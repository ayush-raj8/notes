package oop.override.exam;

// ANCHOR: predict-static-hide
class StaticHideA {
  static String s() {
    return "A.s";
  }

  String i() {
    return "A.i";
  }
}

class StaticHideB extends StaticHideA {
  static String s() {
    return "B.s";
  }

  @Override
  String i() {
    return "B.i";
  }
}

public final class PredictStaticHide {
  public static void main(String[] args) {
    StaticHideA a = new StaticHideB();
    System.out.println(a.s());
    System.out.println(a.i());
    StaticHideB b = (StaticHideB) a;
    System.out.println(b.s());
  }
}
// ANCHOR_END: predict-static-hide
