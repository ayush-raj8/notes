package oop.access.other;

import oop.access.AccessLevels;

/** Intentionally invalid: protected member read from another package, not a subclass. */
public final class AccessProtectedBroken {
  public static void main(String[] args) {
    AccessLevels a = new AccessLevels();
    System.out.println(a.prot);
  }
}
