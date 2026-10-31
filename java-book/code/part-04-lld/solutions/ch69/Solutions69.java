import java.time.Duration;
import java.util.*;
import lld.kv.*;

/** Exercise solutions for chapter 69. */
public class Solutions69 {
  /** Ex 1: set with TTL only if key is absent or expired. */
  static boolean putIfAbsentTtl(KvStore<String, String> store, String key, String value, Duration ttl) {
    if (store.containsKey(key)) {
      return false;
    }
    store.put(key, value, ttl);
    return true;
  }

  /** Ex 2: multi-get; missing → empty Optional. */
  static Map<String, Optional<String>> mget(KvStore<String, String> store, List<String> keys) {
    Map<String, Optional<String>> out = new LinkedHashMap<>();
    for (String k : keys) {
      out.put(k, store.get(k));
    }
    return out;
  }

  /** Ex 3: rename inside a transaction (atomic). */
  static boolean rename(TxKvStore<String, String> store, String from, String to) {
    try (Transaction<String, String> tx = store.begin()) {
      Optional<String> v = tx.get(from);
      if (v.isEmpty()) {
        return false;
      }
      tx.put(to, v.get());
      tx.delete(from);
      tx.commit();
      return true;
    }
  }

  /** Ex 4: increment via CAS loop. */
  static int incr(MapKvStore<String, Integer> store, String key) {
    while (true) {
      Optional<Integer> cur = store.get(key);
      int next = cur.orElse(0) + 1;
      if (cur.isEmpty()) {
        if (store.putIfAbsent(key, next)) {
          return next;
        }
      } else if (store.compareAndSet(key, cur.get(), next)) {
        return next;
      }
    }
  }

  /** Ex 5: keys with prefix that are still live (delegates to static helper). */
  static List<String> livePrefix(KvStore<String, ?> store, String prefix) {
    return KvStore.keysWithPrefix(store, prefix);
  }

  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    MapKvStore<String, String> raw = new MapKvStore<>(clock);
    TxKvStore<String, String> txStore = new TxKvStore<>(raw);

    System.out.println("ex1 " + putIfAbsentTtl(raw, "k", "v", Duration.ofSeconds(5)) + " " + putIfAbsentTtl(raw, "k", "x", Duration.ofSeconds(5)));
    raw.put("a", "1");
    raw.put("b", "2", Duration.ofSeconds(1));
    clock.advance(Duration.ofSeconds(1));
    System.out.println("ex2 " + mget(raw, List.of("a", "b", "c")));

    txStore.put("old", "val");
    System.out.println("ex3 " + rename(txStore, "old", "new") + " " + txStore.get("new") + " " + txStore.get("old"));

    MapKvStore<String, Integer> nums = new MapKvStore<>();
    System.out.println("ex4 " + incr(nums, "n") + " " + incr(nums, "n") + " " + nums.get("n"));

    raw.put("user:1", "a");
    raw.put("user:2", "b");
    raw.put("ord:1", "o");
    System.out.println("ex5 " + livePrefix(raw, "user:"));
  }
}
