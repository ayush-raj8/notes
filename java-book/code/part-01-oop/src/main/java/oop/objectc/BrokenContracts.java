package oop.objectc;

import java.util.HashSet;
import java.util.Set;

// ANCHOR: broken-contracts
/** Compiles, but breaks the contract at run time: equals without hashCode, and the reverse. */
public final class BrokenContracts {
  static final class OnlyEquals {
    final int id;

    OnlyEquals(int id) {
      this.id = id;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof OnlyEquals e && e.id == id;
    }
  }

  static final class OnlyHash {
    final int id;

    OnlyHash(int id) {
      this.id = id;
    }

    @Override
    public int hashCode() {
      return id;
    }
  }

  public static void main(String[] args) {
    Set<OnlyEquals> a = new HashSet<>();
    a.add(new OnlyEquals(1));
    System.out.println("equal objects: " + new OnlyEquals(1).equals(new OnlyEquals(1)));
    System.out.println("set contains equal: " + a.contains(new OnlyEquals(1)));

    Set<OnlyHash> b = new HashSet<>();
    b.add(new OnlyHash(1));
    b.add(new OnlyHash(1));
    System.out.println("same hash, no equals, size: " + b.size());
  }
}
// ANCHOR_END: broken-contracts
