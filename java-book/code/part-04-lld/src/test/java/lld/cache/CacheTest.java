package lld.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

// ANCHOR: cache-test
class CacheTest {
  @Test
  void lruEvictsLeastRecentlyUsed() {
    Cache<String, Integer> c = new LruCache<>(2);
    c.put("a", 1);
    c.put("b", 2);
    c.get("a");
    c.put("c", 3);
    assertEquals(Optional.empty(), c.get("b"));
    assertEquals(Optional.of(1), c.get("a"));
    assertEquals(2, c.size());
  }

  @Test
  void putOnExistingKeyRefreshesRecencyAndValue() {
    Cache<String, String> c = new LruCache<>(2);
    c.put("a", "old");
    c.put("b", "b");
    c.put("a", "new");
    c.put("c", "c");
    assertEquals(Optional.of("new"), c.get("a"));
    assertEquals(Optional.empty(), c.get("b"));
  }

  @Test
  void lfuEvictsLowestFrequencyThenOldest() {
    Cache<String, Integer> c = new LfuCache<>(2);
    c.put("a", 1);
    c.put("b", 2);
    c.get("a");
    c.put("c", 3);
    assertEquals(Optional.empty(), c.get("b"));
    assertEquals(Optional.of(1), c.get("a"));

    Cache<String, Integer> tie = new LfuCache<>(2);
    tie.put("a", 1);
    tie.put("b", 2);
    tie.put("c", 3);
    assertEquals(Optional.empty(), tie.get("a"));
  }

  @Test
  void lfuKeepsFrequentKeyAgainstRecentOne() {
    Cache<String, Integer> lfu = new LfuCache<>(2);
    Cache<String, Integer> lru = new LruCache<>(2);
    for (Cache<String, Integer> c : List.of(lfu, lru)) {
      c.put("hot", 1);
      c.get("hot");
      c.get("hot");
      c.put("x", 2);
      c.put("y", 3);
    }
    assertTrue(lfu.get("hot").isPresent());
    assertTrue(lru.get("hot").isEmpty());
  }

  @Test
  void rejectsBadArguments() {
    assertThrows(IllegalArgumentException.class, () -> new LruCache<String, String>(0));
    assertThrows(IllegalArgumentException.class, () -> new LfuCache<String, String>(-1));
    Cache<String, String> c = new LruCache<>(1);
    assertThrows(NullPointerException.class, () -> c.put(null, "v"));
    assertThrows(NullPointerException.class, () -> c.put("k", null));
  }

  @Test
  void handWrittenLruMatchesLinkedHashMapVersion() {
    Random r = new Random(7);
    Cache<Integer, Integer> mine = new LruCache<>(5);
    Cache<Integer, Integer> jdk = new LinkedHashLruCache<>(5);
    for (int i = 0; i < 2000; i++) {
      int k = r.nextInt(9);
      if (r.nextBoolean()) {
        mine.put(k, i);
        jdk.put(k, i);
      } else {
        assertEquals(jdk.get(k), mine.get(k));
      }
    }
    assertEquals(jdk.size(), mine.size());
  }

  @Test
  void statsCountHitsAndMisses() {
    StatsCache<String, Integer> c = new StatsCache<>(new LruCache<>(1));
    c.put("a", 1);
    c.get("a");
    c.get("z");
    assertEquals(new CacheStats(1, 1), c.stats());
    assertEquals(0.5, c.stats().hitRate());
  }

  @Test
  void threadSafeWrapperKeepsInvariantsUnderContention() throws Exception {
    Cache<Integer, Integer> c = new ThreadSafeCache<>(new LruCache<>(50));
    ExecutorService pool = Executors.newFixedThreadPool(8);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    for (int t = 0; t < 8; t++) {
      int seed = t;
      pool.submit(() -> {
        try {
          Random r = new Random(seed);
          for (int i = 0; i < 20_000; i++) {
            int k = r.nextInt(200);
            if (r.nextInt(3) == 0) {
              c.put(k, i);
            } else {
              c.get(k);
            }
          }
        } catch (Throwable e) {
          failure.set(e);
        }
      });
    }
    pool.shutdown();
    assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
    assertEquals(null, failure.get());
    assertTrue(c.size() <= 50);
  }
}
// ANCHOR_END: cache-test
