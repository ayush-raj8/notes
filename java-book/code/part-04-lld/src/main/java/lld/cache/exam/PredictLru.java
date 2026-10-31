package lld.cache.exam;

import lld.cache.*;

// ANCHOR: predict-lru
public class PredictLru {
  public static void main(String[] args) {
    Cache<Integer, String> c = new LruCache<>(2);
    c.put(1, "one");
    c.put(2, "two");
    c.get(1);
    c.put(3, "three");
    System.out.println(c.get(2));
    System.out.println(c.get(1));
    System.out.println(c.size() + " " + c.capacity());
  }
}
// ANCHOR_END: predict-lru
