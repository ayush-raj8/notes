package oop.overload.exam;

// ANCHOR: predict-widen
public final class PredictWiden {
  static String h(double d) {
    return "double";
  }

  static String h(Object o) {
    return "Object";
  }

  public static void main(String[] args) {
    System.out.println(h(1));
    System.out.println(h("x"));
  }
}
// ANCHOR_END: predict-widen
