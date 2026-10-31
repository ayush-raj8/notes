package oop.objectc.exam;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: predict-mutable-key
public final class PredictMutableKey {
  static final class K {
    int v;

    K(int v) {
      this.v = v;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof K k && k.v == v;
    }

    @Override
    public int hashCode() {
      return v;
    }
  }

  public static void main(String[] args) {
    Map<K, String> m = new HashMap<>();
    K k = new K(1);
    m.put(k, "one");
    k.v = 2;
    System.out.println(m.get(k) + " " + m.get(new K(1)) + " " + m.size());
  }
}
// ANCHOR_END: predict-mutable-key
