package oop.generics;

/** Intentionally invalid: a generic class cannot extend Throwable. */
public final class GenThrowableBroken {
  static class MyError<T> extends Exception {}
}
