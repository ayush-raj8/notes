package oop.access;

import static oop.access.other.Consts.twice;

// ANCHOR: static-import-shadow
/** A method declared in the class hides a static-imported method of the same name. */
public class StaticImportShadowGotcha {
  static int twice(String s) {
    return s.length();
  }

  public static void main(String[] args) {
    System.out.println(twice("abc"));
    System.out.println(oop.access.other.Consts.twice(4));
    // twice(4) here would not compile: the class's own twice(String) hides the import
  }
}
// ANCHOR_END: static-import-shadow
