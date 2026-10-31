package lld.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// ANCHOR: linked-hash-lru
/** The JDK shortcut: access-ordered LinkedHashMap + removeEldestEntry. Same behaviour as LruCache. */
public final class LinkedHashLruCache<K, V> implements Cache<K, V> {
  private final int capacity;
  private final Map<K, V> map;

  public LinkedHashLruCache(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be positive");
    }
    this.capacity = capacity;
    this.map = new LinkedHashMap<>(16, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > LinkedHashLruCache.this.capacity;
      }
    };
  }

  @Override
  public Optional<V> get(K key) {
    return Optional.ofNullable(map.get(Objects.requireNonNull(key)));
  }

  @Override
  public void put(K key, V value) {
    map.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public int capacity() {
    return capacity;
  }
}
// ANCHOR_END: linked-hash-lru
