package oop.hello;

/**
 * Intentionally invalid. Excluded from Maven compile.
 * Demonstrates: overloading by return type alone is not allowed.
 */
public final class HelloBroken {
  public static int size(String value) {
    return value.length();
  }

  public static String size(String value) {
    return String.valueOf(value.length());
  }
}
