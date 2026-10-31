package lld.cache.exam;

import lld.cache.*;

// ANCHOR: predict-lfu
public class PredictLfu {
  public static void main(String[] args) {
    Cache<String, Integer> c = new LfuCache<>(2);
    c.put("a", 1);
    c.put("b", 2);
    c.get("a");
    c.get("a");
    c.get("b");
    c.put("c", 3);
    System.out.println(c.get("a") + " " + c.get("b") + " " + c.get("c"));
  }
}
// ANCHOR_END: predict-lfu
