package oop.init;

/** Intentionally invalid: static field read before its declaration. */
public final class InitStaticBeforeBroken {
  static int a = b + 1;
  static int b = 2;
}
