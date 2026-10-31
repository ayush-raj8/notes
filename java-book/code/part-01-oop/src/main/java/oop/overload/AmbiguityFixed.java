package oop.overload;

// ANCHOR: ambiguity-fixed
/** Fixed forms of classic overload ambiguities: cast or pick a named API. */
public final class AmbiguityFixed {
  public static String both(Object o) {
    return "Object";
  }

  public static String both(String s) {
    return "String";
  }

  public static String num(int n) {
    return "int";
  }

  public static String num(long n) {
    return "long";
  }

  public static void main(String[] args) {
    System.out.println(both((String) null));
    System.out.println(both((Object) null));
    System.out.println(num(1));
    System.out.println(num(1L));
  }
}
// ANCHOR_END: ambiguity-fixed
