package lld.cache;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// ANCHOR: stats-cache
/** Decorator: counts hits and misses of {@code get}. */
public final class StatsCache<K, V> implements Cache<K, V> {
  private final Cache<K, V> delegate;
  private final AtomicLong hits = new AtomicLong();
  private final AtomicLong misses = new AtomicLong();

  public StatsCache(Cache<K, V> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Optional<V> get(K key) {
    Optional<V> r = delegate.get(key);
    (r.isPresent() ? hits : misses).incrementAndGet();
    return r;
  }

  @Override
  public void put(K key, V value) {
    delegate.put(key, value);
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public int capacity() {
    return delegate.capacity();
  }

  public CacheStats stats() {
    return new CacheStats(hits.get(), misses.get());
  }
}
// ANCHOR_END: stats-cache
