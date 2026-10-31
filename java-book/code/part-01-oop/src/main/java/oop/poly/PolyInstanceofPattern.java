package oop.poly;

// ANCHOR: poly-instanceof-pattern
/** instanceof, pattern matching (Java 16+), pattern switch (Java 21). */
public final class PolyInstanceofPattern {
  static String old(Object o) {
    if (o instanceof String) {
      String s = (String) o;
      return "old:" + s.length();
    }
    return "old:other";
  }

  static String modern(Object o) {
    if (o instanceof String s && s.length() > 2) { // s is in scope on the right of &&
      return "long " + s;
    }
    if (!(o instanceof Integer n)) { // n is in scope after this if when it returns
      return "not int";
    }
    return "int " + (n + 1);
  }

  static String sw(Object o) {
    return switch (o) {
      case null -> "null";
      case Integer i when i > 10 -> "big " + i;
      case Integer i -> "int " + i;
      case String s -> "str " + s;
      default -> "other";
    };
  }

  public static void main(String[] args) {
    System.out.println(old("abc") + " " + old(1));
    System.out.println(modern("abcd"));
    System.out.println(modern("ab"));
    System.out.println(modern(5));
    System.out.println(modern(null));
    System.out.println(sw(42) + ", " + sw(7) + ", " + sw("x") + ", " + sw(null) + ", " + sw(1.5));
    System.out.println(null instanceof Object);
  }
}
// ANCHOR_END: poly-instanceof-pattern
