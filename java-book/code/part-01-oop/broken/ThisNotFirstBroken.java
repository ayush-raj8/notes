package oop.ctors;

/** Intentionally invalid: {@code this(...)} is not the first statement. */
public final class ThisNotFirstBroken {
  private final int n;

  public ThisNotFirstBroken(int n) {
    System.out.println("before");
    this(n, 0);
  }

  public ThisNotFirstBroken(int n, int ignored) {
    this.n = n;
  }
}
