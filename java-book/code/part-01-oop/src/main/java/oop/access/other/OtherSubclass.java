package oop.access.other;

import oop.access.AccessLevels;

// ANCHOR: other-subclass
/** Subclass in another package: {@code protected} is visible through the subclass type only. */
public class OtherSubclass extends AccessLevels {
  public String viaInheritance() {
    return pub + "," + prot;
  }

  public String viaSameType(OtherSubclass other) {
    return other.prot;
  }

  public static void main(String[] args) {
    OtherSubclass s = new OtherSubclass();
    System.out.println(s.viaInheritance());
    System.out.println(s.viaSameType(new OtherSubclass()));
  }
}
// ANCHOR_END: other-subclass
