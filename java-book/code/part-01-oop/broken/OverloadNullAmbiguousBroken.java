package oop.overload;

/** Ambiguous: null matches String and Object equally? Actually String is more specific.
 * Classic ambiguity: two unrelated reference types. */
public final class OverloadNullAmbiguousBroken {
  public static void f(String s) {}

  public static void f(StringBuilder sb) {}

  public static void main(String[] args) {
    f(null);
  }
}
