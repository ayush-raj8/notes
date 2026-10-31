package oop.poly.exam;

// ANCHOR: predict-pattern-scope
public final class PredictPatternScope {
  static int len(Object o) {
    if (!(o instanceof String s)) {
      return -1;
    }
    return s.length();
  }

  static String empty(Object o) {
    return (o instanceof String s && s.isEmpty()) ? "empty" : "no";
  }

  public static void main(String[] args) {
    System.out.println(len("four") + " " + len(4) + " " + len(null));
    System.out.println(empty("") + " " + empty("x") + " " + empty(null));
  }
}
// ANCHOR_END: predict-pattern-scope
