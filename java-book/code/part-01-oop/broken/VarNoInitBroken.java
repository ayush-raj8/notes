package oop.memory;

/** Intentionally invalid: {@code var} local must have an initializer. */
public final class VarNoInitBroken {
  public static void demo() {
    var x;
    x = 1;
    System.out.println(x);
  }
}
