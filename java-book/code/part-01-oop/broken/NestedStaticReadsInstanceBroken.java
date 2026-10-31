package oop.nested;

/** Intentionally invalid: a static nested class has no outer instance. */
public final class NestedStaticReadsInstanceBroken {
  private int x = 5;

  static class Peek {
    int read() {
      return x;
    }
  }
}
