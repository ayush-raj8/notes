package lld.cache.exam;

import lld.cache.*;

// ANCHOR: predict-update
public class PredictUpdate {
  public static void main(String[] args) {
    Cache<String, String> c = new LruCache<>(2);
    c.put("a", "old");
    c.put("b", "b");
    c.put("a", "new");
    c.put("c", "c");
    System.out.println(c.get("a"));
    System.out.println(c.get("b"));
    System.out.println(c.size());
  }
}
// ANCHOR_END: predict-update
