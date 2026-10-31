package lld.cache;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

// ANCHOR: thread-safe-cache
/**
 * Decorator: one lock around every call. {@code get} mutates recency, so even reads need the
 * exclusive lock; a read-write lock would not help.
 */
public final class ThreadSafeCache<K, V> implements Cache<K, V> {
  private final Cache<K, V> delegate;
  private final ReentrantLock lock = new ReentrantLock();

  public ThreadSafeCache(Cache<K, V> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Optional<V> get(K key) {
    lock.lock();
    try {
      return delegate.get(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void put(K key, V value) {
    lock.lock();
    try {
      delegate.put(key, value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int size() {
    lock.lock();
    try {
      return delegate.size();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int capacity() {
    return delegate.capacity();
  }
}
// ANCHOR_END: thread-safe-cache
