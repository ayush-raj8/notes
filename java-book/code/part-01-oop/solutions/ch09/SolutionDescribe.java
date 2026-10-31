package oop.poly.solutions;

/** Exercise 2: instanceof with pattern variables, no manual casts. */
public final class SolutionDescribe {
  static String describe(Object o) {
    if (o instanceof Integer i && i < 0) {
      return "negative int";
    } else if (o instanceof Integer i) {
      return "int " + i;
    } else if (o instanceof String s && !s.isEmpty()) {
      return "string of " + s.length();
    } else if (o instanceof String) {
      return "empty string";
    }
    return "unknown";
  }

  public static void main(String[] args) {
    System.out.println(describe(-4));
    System.out.println(describe(4));
    System.out.println(describe("abc"));
    System.out.println(describe(""));
    System.out.println(describe(2.0));
  }
}
