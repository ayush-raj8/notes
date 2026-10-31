package oop.nested;

/** Intentionally invalid: an anonymous class has no name, so it cannot declare a constructor. */
public final class NestedAnonConstructorBroken {
  static class Base {}

  public static void main(String[] args) {
    Object o =
        new Base() {
          Base() {}
        };
    System.out.println(o);
  }
}
