package oop.overload;

// ANCHOR: overload-basics
/** Allowed: different parameter count, types, or order. */
public final class OverloadBasics {
  public static String describe(int n) {
    return "int:" + n;
  }

  public static String describe(long n) {
    return "long:" + n;
  }

  public static String describe(int a, int b) {
    return "ii:" + a + "," + b;
  }

  public static String describe(String s, int n) {
    return "si:" + s + "," + n;
  }

  public static String describe(int n, String s) {
    return "is:" + n + "," + s;
  }

  public static void main(String[] args) {
    System.out.println(describe(1));
    System.out.println(describe(1L));
    System.out.println(describe(1, 2));
    System.out.println(describe("a", 1));
    System.out.println(describe(1, "a"));
  }
}
// ANCHOR_END: overload-basics
