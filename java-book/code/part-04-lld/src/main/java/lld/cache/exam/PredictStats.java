package lld.cache.exam;

import lld.cache.*;

// ANCHOR: predict-stats
public class PredictStats {
  public static void main(String[] args) {
    StatsCache<String, Integer> c = new StatsCache<>(new LruCache<>(1));
    c.put("a", 1);
    c.get("a");
    c.get("b");
    c.put("b", 2);
    c.get("a");
    System.out.println(c.stats());
    System.out.println(c.stats().hitRate());
  }
}
// ANCHOR_END: predict-stats
