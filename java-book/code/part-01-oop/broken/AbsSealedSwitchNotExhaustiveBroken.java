package oop.abstracts;

sealed interface SneFigure permits SneDisc, SneBox {}

record SneDisc(double r) implements SneFigure {}

record SneBox(double w) implements SneFigure {}

/** Not allowed: a switch expression over a sealed type must cover every permitted subtype. */
public class AbsSealedSwitchNotExhaustiveBroken {
  static String f(SneFigure s) {
    return switch (s) {
      case SneDisc d -> "disc";
    };
  }

  public static void main(String[] args) {
    System.out.println(f(new SneDisc(1)));
  }
}
