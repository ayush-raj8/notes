package oop.uml;

/** Intentionally invalid: a non-underlined (instance) operation needs an object. */
public final class UmlStaticContextBroken {
  static class Counter {
    int count;

    void inc() {
      count++;
    }
  }

  static void reset() {
    Counter.inc();
  }
}
