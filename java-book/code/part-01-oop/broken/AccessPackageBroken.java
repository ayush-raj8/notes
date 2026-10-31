package oop.access.other;

import oop.access.AccessLevels;

/** Intentionally invalid: package-private member read from another package. */
public final class AccessPackageBroken {
  public static void main(String[] args) {
    AccessLevels a = new AccessLevels();
    System.out.println(a.pkg);
  }
}
