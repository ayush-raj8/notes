package oop.compare.solutions;

import java.util.HashSet;
import java.util.Set;

/** Exercise 1: make equals a real override so collections see it. */
public final class SolutionEqualsOverride {
  static final class Id {
    final int n;

    Id(int n) {
      this.n = n;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Id other && other.n == n;
    }

    @Override
    public int hashCode() {
      return Integer.hashCode(n);
    }
  }

  public static void main(String[] args) {
    Set<Id> set = new HashSet<>();
    set.add(new Id(1));
    System.out.println(set.contains(new Id(1)));
    Object o = new Id(1);
    System.out.println(new Id(1).equals(o));
  }
}
