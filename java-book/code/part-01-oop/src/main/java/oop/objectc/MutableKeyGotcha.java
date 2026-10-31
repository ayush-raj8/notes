package oop.objectc;

import java.util.HashSet;
import java.util.Set;

// ANCHOR: mutable-key
/** Mutating a field that feeds hashCode after insertion loses the element. */
public final class MutableKeyGotcha {
  static final class Key {
    int id;

    Key(int id) {
      this.id = id;
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
    k.id = 2;
    System.out.println("contains(k)=" + set.contains(k));
    System.out.println("size=" + set.size());
    System.out.println("iteration sees it=" + set.iterator().next().equals(k));
  }
}
// ANCHOR_END: mutable-key
