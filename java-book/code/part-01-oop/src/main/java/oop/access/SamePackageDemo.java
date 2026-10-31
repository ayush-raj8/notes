package oop.access;

// ANCHOR: same-package
/** Same package as {@link AccessLevels}: public, protected, and package-private are visible. */
public class SamePackageDemo {
  public static String visible() {
    AccessLevels a = new AccessLevels();
    return a.pub + "," + a.prot + "," + a.pkg;
    // a.priv would not compile here (see AccessPrivateBroken)
  }

  public static void main(String[] args) {
    System.out.println(visible());
    System.out.println(PackagePrivateHelper.hello());
  }
}
// ANCHOR_END: same-package
