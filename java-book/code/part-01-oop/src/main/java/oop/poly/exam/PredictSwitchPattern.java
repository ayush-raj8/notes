package oop.poly.exam;

import java.util.List;

// ANCHOR: predict-switch-pattern
public final class PredictSwitchPattern {
  static String f(Object o) {
    return switch (o) {
      case Integer i when i > 5 -> "big";
      case Integer i -> "int";
      case CharSequence cs -> "cs" + cs.length();
      case Number n -> "num";
      default -> "other";
    };
  }

  public static void main(String[] args) {
    System.out.println(f(9) + " " + f(3) + " " + f("ab") + " " + f(2.5));
    System.out.println(f(new StringBuilder("xyz")) + " " + f(List.of()));
    try {
      f(null);
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
  }
}
// ANCHOR_END: predict-switch-pattern
