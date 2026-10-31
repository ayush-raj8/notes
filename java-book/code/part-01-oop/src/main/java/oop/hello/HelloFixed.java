package oop.hello;

// ANCHOR: hello-fixed
/** Fixed version of the broken return-type-only overload. */
public final class HelloFixed {
  public static int size(String value) {
    return value.length();
  }

  public static int size(String value, boolean trim) {
    return trim ? value.trim().length() : value.length();
  }

  public static void main(String[] args) {
    System.out.println(size("  ab  "));
    System.out.println(size("  ab  ", true));
  }
}
// ANCHOR_END: hello-fixed
