package lld.cache;

// ANCHOR: cache-demo
public final class CacheDemo {
  private CacheDemo() {}

  public static void main(String[] args) {
    Cache<String, Integer> lru = new LruCache<>(2);
    lru.put("a", 1);
    lru.put("b", 2);
    lru.get("a");
    lru.put("c", 3);
    System.out.println("LRU  a=" + lru.get("a") + " b=" + lru.get("b") + " c=" + lru.get("c"));

    Cache<String, Integer> lfu = new LfuCache<>(2);
    lfu.put("a", 1);
    lfu.put("b", 2);
    lfu.get("a");
    lfu.get("a");
    lfu.get("b");
    lfu.put("c", 3);
    System.out.println("LFU  a=" + lfu.get("a") + " b=" + lfu.get("b") + " c=" + lfu.get("c"));

    StatsCache<Integer, String> scan = new StatsCache<>(new LruCache<>(2));
    for (int round = 0; round < 3; round++) {
      for (int k = 1; k <= 3; k++) {
        if (scan.get(k).isEmpty()) {
          scan.put(k, "v" + k);
        }
      }
    }
    System.out.println("scan " + scan.stats() + " rate=" + scan.stats().hitRate());
  }
}
// ANCHOR_END: cache-demo
