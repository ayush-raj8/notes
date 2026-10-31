package oop.ctors;

/** Intentionally invalid: recursive {@code this()} with no terminal constructor. */
public final class RecursiveThisBroken {
  public RecursiveThisBroken() {
    this(1);
  }

  public RecursiveThisBroken(int n) {
    this();
  }
}
