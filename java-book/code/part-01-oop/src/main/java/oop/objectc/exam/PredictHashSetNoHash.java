package oop.objectc.exam;

import java.util.HashSet;
import java.util.Set;

// ANCHOR: predict-hashset-no-hash
public final class PredictHashSetNoHash {
  static final class Id {
    final int n;

    Id(int n) {
      this.n = n;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Id i && i.n == n;
    }
  }

  public static void main(String[] args) {
    Set<Id> s = new HashSet<>();
    s.add(new Id(7));
    s.add(new Id(7));
    System.out.println(s.size() + " " + s.contains(new Id(7)));
  }
}
// ANCHOR_END: predict-hashset-no-hash
