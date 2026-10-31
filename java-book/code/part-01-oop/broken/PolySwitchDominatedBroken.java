package oop.poly;

/** A general pattern before a specific one makes the specific one unreachable. */
public class PolySwitchDominatedBroken {
  static String f(Object o) {
    return switch (o) {
      case CharSequence cs -> "cs";
      case String s -> "string";
      default -> "other";
    };
  }
}
