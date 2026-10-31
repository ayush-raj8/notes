package oop.init;

/** Intentionally invalid: static block touches an instance field. */
public final class InitStaticUsesInstanceBroken {
  int count = 0;

  static {
    count = 1;
  }
}
