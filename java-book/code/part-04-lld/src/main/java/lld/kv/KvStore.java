package lld.kv;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// ANCHOR: kv-store
/**
 * Contract of an in-memory key-value store. Values are never {@code null}: absence is
 * {@link Optional#empty()}, so {@code get} cannot confuse "missing" with "stored null".
 */
public interface KvStore<K, V> {
  Optional<V> get(K key);

  /** Stores the value forever; returns the previous live value, if any. */
  Optional<V> put(K key, V value);

  /** Stores the value for {@code ttl}; the entry is invisible once the TTL has elapsed. */
  Optional<V> put(K key, V value, Duration ttl);

  /** Atomically stores only when no live value exists; true when this call stored it. */
  boolean putIfAbsent(K key, V value);

  /** Atomically replaces {@code expected} with {@code update}; the TTL is kept. */
  boolean compareAndSet(K key, V expected, V update);

  boolean delete(K key);

  boolean containsKey(K key);

  /** Number of live (unexpired) entries. */
  int size();

  /** Snapshot of the live keys; later changes to the store do not change the returned set. */
  Set<K> keys();

  default V getOrDefault(K key, V fallback) {
    return get(key).orElse(fallback);
  }

  /** Static interface method: call it on the type, {@code KvStore.keysWithPrefix(...)}. */
  static <V> List<String> keysWithPrefix(KvStore<String, V> store, String prefix) {
    return store.keys().stream().filter(k -> k.startsWith(prefix)).sorted().toList();
  }
}
// ANCHOR_END: kv-store
