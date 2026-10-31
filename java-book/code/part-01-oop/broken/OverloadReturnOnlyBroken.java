package oop.overload;

/** Not allowed: overload by return type only. */
public final class OverloadReturnOnlyBroken {
  public static int size(String s) {
    return s.length();
  }

  public static String size(String s) {
    return String.valueOf(s.length());
  }
}
