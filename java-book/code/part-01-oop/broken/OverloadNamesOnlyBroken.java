package oop.overload;

/** Not allowed: parameter names are not part of the signature. */
public final class OverloadNamesOnlyBroken {
  public static int add(int a, int b) {
    return a + b;
  }

  public static int add(int x, int y) {
    return x + y;
  }
}
