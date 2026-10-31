package oop.objectc.solutions;

import java.util.HashSet;
import java.util.Set;

/** Exercise 5: make the key immutable so the set stays consistent. */
public final class SolutionFixMutableKey {
  static final class Key {
    private final int id;

    Key(int id) {
      this.id = id;
    }

    Key withId(int next) {
      return new Key(next);
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Key k && k.id == id;
    }

    @Override
    public int hashCode() {
      return Integer.hashCode(id);
    }
  }

  public static void main(String[] args) {
    Set<Key> set = new HashSet<>();
    Key k = new Key(1);
    set.add(k);
    Key moved = k.withId(2);
    System.out.println(set.contains(k) + " " + set.contains(moved));
  }
}
