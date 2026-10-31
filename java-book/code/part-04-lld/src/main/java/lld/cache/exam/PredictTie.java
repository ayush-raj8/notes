package lld.cache.exam;

import lld.cache.*;

// ANCHOR: predict-tie
public class PredictTie {
  public static void main(String[] args) {
    Cache<String, Integer> c = new LfuCache<>(2);
    c.put("a", 1);
    c.put("b", 2);
    c.put("c", 3);
    System.out.println(c.get("a").isPresent() + " " + c.get("b").isPresent() + " " + c.get("c").isPresent());
  }
}
// ANCHOR_END: predict-tie
