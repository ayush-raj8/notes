package oop.abstracts;

// ANCHOR: sealed-shapes
sealed interface Figure permits Disc, Box, Blob {}

record Disc(double r) implements Figure {}

record Box(double w, double h) implements Figure {}

non-sealed class Blob implements Figure {}

class BlobWithHole extends Blob {} // allowed: Blob is non-sealed

/** Java 17+: sealed restricts who may extend; the switch needs no default. */
public final class SealedShapes {
  static String describe(Figure f) {
    return switch (f) {
      case Disc d -> "disc r=" + d.r();
      case Box b -> "box " + b.w() + "x" + b.h();
      case Blob b -> "blob " + b.getClass().getSimpleName();
    };
  }

  public static void main(String[] args) {
    System.out.println(describe(new Disc(2)));
    System.out.println(describe(new Box(2, 3)));
    System.out.println(describe(new BlobWithHole()));
    System.out.println(Figure.class.isSealed());
    for (Class<?> c : Figure.class.getPermittedSubclasses()) {
      System.out.println(c.getSimpleName());
    }
  }
}
// ANCHOR_END: sealed-shapes
