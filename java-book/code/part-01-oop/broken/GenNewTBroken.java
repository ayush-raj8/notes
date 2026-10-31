package oop.generics;

/** Intentionally invalid: T is erased; there is nothing to instantiate. */
public final class GenNewTBroken {
  static <T> T make() {
    return new T();
  }
}
