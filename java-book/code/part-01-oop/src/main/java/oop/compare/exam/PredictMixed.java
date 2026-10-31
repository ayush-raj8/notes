package oop.compare.exam;

// ANCHOR: predict-mixed
class P6A {
  String f = "A";

  String m(int i) {
    return "A.int";
  }

  String m(long l) {
    return "A.long";
  }

  static String s() {
    return "A.s";
  }

  String call() {
    return m(1) + "," + s() + "," + f;
  }
}

class P6B extends P6A {
  String f = "B";

  @Override
  String m(int i) {
    return "B.int";
  }

  static String s() {
    return "B.s";
  }
}

public final class PredictMixed {
  public static void main(String[] args) {
    P6A a = new P6B();
    System.out.println(a.call());
    System.out.println(a.m(1L));
    System.out.println(a.m('c'));
    System.out.println(((P6B) a).f + a.f);
  }
}
// ANCHOR_END: predict-mixed
