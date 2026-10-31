package oop.init;

/** Intentionally invalid: instance field read before its declaration. */
public final class InitForwardInstanceBroken {
  int x = y;
  int y = 1;
}
