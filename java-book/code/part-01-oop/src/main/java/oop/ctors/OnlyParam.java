package oop.ctors;

// ANCHOR: only-param
/**
 * Declaring any constructor suppresses the compiler-generated default. Callers must use a declared
 * constructor (see fixed factory / explicit no-arg in {@link OnlyParamFixed}).
 */
public final class OnlyParam {
  private final int n;

  public OnlyParam(int n) {
    this.n = n;
  }

  public int n() {
    return n;
  }
}
// ANCHOR_END: only-param
