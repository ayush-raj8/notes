package oop.overload.exam;

// ANCHOR: predict-varargs
public final class PredictVarargs {
  static String g(int a) {
    return "one";
  }

  static String g(int... a) {
    return "var:" + a.length;
  }

  public static void main(String[] args) {
    System.out.println(g(1));
    System.out.println(g(1, 2));
  }
}
// ANCHOR_END: predict-varargs
