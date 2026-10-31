package oop.overload;

// ANCHOR: resolution-order
/** Resolution preference: exact > widening > boxing > varargs. */
public final class ResolutionOrder {
  public static String pick(int n) {
    return "exact-int";
  }

  public static String pick(long n) {
    return "widen-long";
  }

  public static String pick(Integer n) {
    return "box-Integer";
  }

  public static String pick(int... n) {
    return "varargs";
  }

  public static void main(String[] args) {
    System.out.println(pick(1));
    System.out.println(pick(Integer.valueOf(1)));
    System.out.println(pick());
    System.out.println(pick(1, 2));
  }
}
// ANCHOR_END: resolution-order
