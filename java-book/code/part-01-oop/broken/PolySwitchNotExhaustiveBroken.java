package oop.poly;

/** A pattern switch must cover every input; Object needs a default or an Object pattern. */
public class PolySwitchNotExhaustiveBroken {
  static String f(Object o) {
    return switch (o) {
      case String s -> "string";
      case Integer i -> "int";
    };
  }
}
