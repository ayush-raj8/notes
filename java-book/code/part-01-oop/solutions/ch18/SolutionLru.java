package oop.coll.solutions;

import java.util.LinkedHashMap;
import java.util.Map;

/** Exercise 2: capacity-3 LRU cache with LinkedHashMap. */
public final class SolutionLru {
  static <K, V> Map<K, V> lru(int capacity) {
    return new LinkedHashMap<>(16, 0.75f, true) {
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
      }
    };
  }

  public static void main(String[] args) {
    Map<Integer, String> c = lru(3);
    c.put(1, "a");
    c.put(2, "b");
    c.put(3, "c");
    c.get(1);
    c.put(4, "d");
    System.out.println(c.keySet());
  }
}
