package oop.poly.solutions;

/** Exercise 3: pattern switch with a guard; case order matters. */
public final class SolutionSwitchGuard {
  static String size(Object o) {
    return switch (o) {
      case null -> "none";
      case String s when s.length() > 3 -> "long text";
      case String s -> "short text";
      case Integer i when i > 100 -> "big number";
      case Integer i -> "number";
      default -> "other";
    };
  }

  public static void main(String[] args) {
    System.out.println(size("hello"));
    System.out.println(size("hi"));
    System.out.println(size(500));
    System.out.println(size(5));
    System.out.println(size(null));
    System.out.println(size(1.5));
  }
}
