package oop.access.other;

// ANCHOR: consts
/** Static members meant for {@code import static}. */
public final class Consts {
  public static final double RATE = 0.5;

  private Consts() {}

  public static int twice(int n) {
    return 2 * n;
  }
}
// ANCHOR_END: consts
