package lld.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// ANCHOR: lfu-cache
/**
 * Least frequently used, O(1): key to frequency, frequency to keys in insertion order. Ties are
 * broken by least recently used within the lowest frequency.
 */
public final class LfuCache<K, V> implements Cache<K, V> {
  private final int capacity;
  private final Map<K, V> values = new HashMap<>();
  private final Map<K, Integer> frequency = new HashMap<>();
  private final Map<Integer, LinkedHashSet<K>> buckets = new HashMap<>();
  private int minFrequency;

  public LfuCache(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be positive");
    }
    this.capacity = capacity;
  }

  @Override
  public Optional<V> get(K key) {
    Objects.requireNonNull(key);
    if (!values.containsKey(key)) {
      return Optional.empty();
    }
    touch(key);
    return Optional.of(values.get(key));
  }

  @Override
  public void put(K key, V value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    if (values.containsKey(key)) {
      values.put(key, value);
      touch(key);
      return;
    }
    if (values.size() == capacity) {
      evict();
    }
    values.put(key, value);
    frequency.put(key, 1);
    buckets.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(key);
    minFrequency = 1;
  }

  private void touch(K key) {
    int f = frequency.get(key);
    LinkedHashSet<K> bucket = buckets.get(f);
    bucket.remove(key);
    if (bucket.isEmpty()) {
      buckets.remove(f);
      if (minFrequency == f) {
        minFrequency = f + 1;
      }
    }
    frequency.put(key, f + 1);
    buckets.computeIfAbsent(f + 1, x -> new LinkedHashSet<>()).add(key);
  }

  private void evict() {
    LinkedHashSet<K> bucket = buckets.get(minFrequency);
    K victim = bucket.iterator().next();
    bucket.remove(victim);
    if (bucket.isEmpty()) {
      buckets.remove(minFrequency);
    }
    values.remove(victim);
    frequency.remove(victim);
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public int capacity() {
    return capacity;
  }
}
// ANCHOR_END: lfu-cache
