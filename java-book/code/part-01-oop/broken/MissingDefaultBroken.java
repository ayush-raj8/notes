package oop.ctors;

/**
 * Intentionally invalid usage: {@link OnlyParam} has a parameterized constructor only, so the
 * default no-arg constructor does not exist.
 */
public final class MissingDefaultBroken {
  public static void main(String[] args) {
    OnlyParam x = new OnlyParam();
    System.out.println(x.n());
  }
}
