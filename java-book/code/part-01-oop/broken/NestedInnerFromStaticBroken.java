package oop.nested;

/** Intentionally invalid: creating an inner class from static context without an outer instance. */
public final class NestedInnerFromStaticBroken {
  class Inner {}

  public static void main(String[] args) {
    Inner i = new Inner();
    System.out.println(i);
  }
}
