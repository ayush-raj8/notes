package oop.memory;

/**
 * Intentionally invalid: {@code var} local needs an initializer that is not the null literal alone
 * (compiler cannot infer a type from {@code null}).
 */
public final class VarNullBroken {
  public static void demo() {
    var x = null;
    System.out.println(x);
  }
}
