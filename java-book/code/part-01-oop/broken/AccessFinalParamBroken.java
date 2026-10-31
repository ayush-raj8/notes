package oop.access;

/** Intentionally invalid: assignment to a final parameter. */
public final class AccessFinalParamBroken {
  static int bump(final int n) {
    n = n + 1;
    return n;
  }
}
