package oop.generics;

/** Intentionally invalid: generic array creation. */
public final class GenNewArrayBroken {
  static <T> T[] make(int n) {
    return new T[n];
  }
}
