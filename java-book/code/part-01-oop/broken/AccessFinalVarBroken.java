package oop.access;

/** Intentionally invalid: second assignment to a final variable. */
public final class AccessFinalVarBroken {
  public static void main(String[] args) {
    final int x = 1;
    x = 2;
    System.out.println(x);
  }
}
