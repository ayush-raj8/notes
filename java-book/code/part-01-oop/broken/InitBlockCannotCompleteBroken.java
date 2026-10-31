package oop.init;

/** Intentionally invalid: static initializer always throws. */
public final class InitBlockCannotCompleteBroken {
  static {
    throw new RuntimeException("boom");
  }
}
