package oop.ctors;

// ANCHOR: recursive-this-gotcha
/**
 * Gotcha shape (do not run in a loop forever in CI): mutual {@code this()} without a terminal
 * constructor is a compile-time error in Java — shown as {@link oop.ctors.exam.PredictRecursiveThis}
 * compiling form that terminates. This class documents the safe pattern: one ctor must not call
 * this() endlessly.
 */
public final class RecursiveThisGotcha {
  private final int n;

  public RecursiveThisGotcha(int n) {
    this.n = n;
  }

  public RecursiveThisGotcha() {
    this(0);
  }

  public int n() {
    return n;
  }
}
// ANCHOR_END: recursive-this-gotcha
