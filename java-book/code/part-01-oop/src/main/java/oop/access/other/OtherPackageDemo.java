package oop.access.other;

import oop.access.AccessLevels;

// ANCHOR: other-package
/** Different package, not a subclass: only {@code public} members are visible. */
public class OtherPackageDemo {
  public static String visible() {
    AccessLevels a = new AccessLevels();
    return a.pub;
    // a.prot, a.pkg, a.priv do not compile here
  }

  public static void main(String[] args) {
    System.out.println(visible());
  }
}
// ANCHOR_END: other-package
