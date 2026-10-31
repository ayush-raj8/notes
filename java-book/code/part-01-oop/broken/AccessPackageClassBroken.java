package oop.access.other;

import oop.access.PackagePrivateHelper;

/** Intentionally invalid: package-private class named from another package. */
public final class AccessPackageClassBroken {
  public static void main(String[] args) {
    System.out.println(PackagePrivateHelper.hello());
  }
}
