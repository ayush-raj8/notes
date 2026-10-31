package oop.immut;

/** Intentionally invalid: a constant without a body cannot satisfy an abstract method. */
public final class ImmutEnumAbstractMissingBroken {
  enum Op {
    PLUS,
    TIMES {
      @Override
      int apply(int a, int b) {
        return a * b;
      }
    };

    abstract int apply(int a, int b);
  }
}
