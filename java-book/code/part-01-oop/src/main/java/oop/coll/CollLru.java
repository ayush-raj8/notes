package oop.coll;

import java.util.LinkedHashMap;
import java.util.Map;

// ANCHOR: coll-lru
/** Choosing the type: a bounded cache that evicts the least recently used key. */
class CollLruCache<K, V> extends LinkedHashMap<K, V> {
  private static final long serialVersionUID = 1L;
  private final int capacity;

  CollLruCache(int capacity) {
    super(16, 0.75f, true); // accessOrder = true: get() moves the entry to the end
    this.capacity = capacity;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > capacity;
  }
}

public final class CollLru {
  public static void main(String[] args) {
    CollLruCache<String, Integer> c = new CollLruCache<>(2);
    c.put("a", 1);
    c.put("b", 2);
    c.get("a"); // "a" becomes most recently used
    c.put("c", 3); // evicts "b"
    System.out.println(c.keySet());
  }
}
// ANCHOR_END: coll-lru
