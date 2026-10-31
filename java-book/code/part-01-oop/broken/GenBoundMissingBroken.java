package oop.generics;

/** Intentionally invalid: unbounded T only has Object's methods. */
public final class GenBoundMissingBroken {
  static <T> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;
  }
}
