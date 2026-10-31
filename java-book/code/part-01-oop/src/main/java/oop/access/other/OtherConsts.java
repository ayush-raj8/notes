package oop.access.other;

/** Second class with a static method named {@code twice(int)}; used to show import ambiguity. */
public final class OtherConsts {
  private OtherConsts() {}

  public static int twice(int n) {
    return 3 * n;
  }
}
