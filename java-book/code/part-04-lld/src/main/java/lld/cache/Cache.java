package lld.cache;

import java.util.Optional;

// ANCHOR: cache
/** A bounded key-value cache. {@code get} counts as a use for eviction purposes. */
public interface Cache<K, V> {
  Optional<V> get(K key);

  void put(K key, V value);

  int size();

  int capacity();
}
// ANCHOR_END: cache
