package oop.coll;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// ANCHOR: coll-mutable-key
class CollKey {
  int id;

  CollKey(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof CollKey k && k.id == id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

public final class CollMutableKey {
  public static void main(String[] args) {
    Map<CollKey, String> m = new HashMap<>();
    CollKey key = new CollKey(1);
    m.put(key, "value");
    System.out.println("before: " + m.get(key));
    key.id = 2; // hashCode changes while the key sits in its old bucket
    System.out.println("after mutate, same object : " + m.get(key));
    System.out.println("after mutate, fresh id=1  : " + m.get(new CollKey(1)));
    System.out.println("size=" + m.size() + ", containsKey(key)=" + m.containsKey(key));
  }
}
// ANCHOR_END: coll-mutable-key
