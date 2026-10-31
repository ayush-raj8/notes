package oop.ex.solutions;

import java.util.OptionalInt;

/** Replace "catch Exception and return 0" with a result that cannot be confused with bad input. */
public final class SolutionSafeParse {
  static OptionalInt parse(String s) {
    try {
      return OptionalInt.of(Integer.parseInt(s.trim()));
    } catch (NumberFormatException e) {
      return OptionalInt.empty();
    }
  }

  public static void main(String[] args) {
    System.out.println(parse(" 42 "));
    System.out.println(parse("x"));
  }
}
