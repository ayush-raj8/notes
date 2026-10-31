import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lld.cache.*;

/** Exercises 1-5 of chapter 55. */
public class SolutionCache {
  // Ex 1: read-through helper.
  static <K, V> V getOrCompute(Cache<K, V> cache, K key, Function<K, V> loader) {
    return cache.get(key).orElseGet(() -> {
      V v = loader.apply(key);
      cache.put(key, v);
      return v;
    });
  }

  // Ex 3: FIFO cache: insertion order, reads do not matter.
  static final class FifoCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> map;

    FifoCache(int capacity) {
      this.capacity = capacity;
      this.map = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> e) {
          return size() > FifoCache.this.capacity;
        }
      };
    }

    public Optional<V> get(K key) { return Optional.ofNullable(map.get(key)); }
    public void put(K key, V value) { map.put(key, value); }
    public int size() { return map.size(); }
    public int capacity() { return capacity; }
  }

  static String run(Cache<String, Integer> c) {
    c.put("a", 1);
    c.get("a");
    c.get("a");
    c.put("b", 2);
    c.put("c", 3);
    return c.get("a").isPresent() + "/" + c.get("b").isPresent() + "/" + c.get("c").isPresent();
  }

  public static void main(String[] args) {
    Cache<Integer, String> c = new LruCache<>(2);
    System.out.println(getOrCompute(c, 1, k -> "loaded" + k) + " " + getOrCompute(c, 1, k -> "again"));

    // Ex 2 + 3: same sequence, three policies (a/b/c present?).
    System.out.println("LRU  " + run(new LruCache<>(2)));
    System.out.println("LFU  " + run(new LfuCache<>(2)));
    System.out.println("FIFO " + run(new FifoCache<>(2)));

    // Ex 4: hand-written LRU equals the JDK-based one on a sequence.
    Cache<Integer, Integer> mine = new LruCache<>(3);
    Cache<Integer, Integer> jdk = new LinkedHashLruCache<>(3);
    boolean same = true;
    int[] keys = {1, 2, 3, 1, 4, 2, 5, 1};
    for (int k : keys) {
      same &= mine.get(k).equals(jdk.get(k));
      mine.put(k, k);
      jdk.put(k, k);
    }
    System.out.println("same=" + same);

    // Ex 5: cyclic scan over 3 keys with capacity 2 -> LRU never hits.
    StatsCache<Integer, Integer> scan = new StatsCache<>(new LruCache<>(2));
    for (int round = 0; round < 4; round++) {
      for (int k = 1; k <= 3; k++) {
        getOrCompute(scan, k, x -> x);
      }
    }
    System.out.println(scan.stats());
  }
}
