package lld.kv;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

// ANCHOR: tx-kv-store
/**
 * Decorator: adds {@link #begin()} to any {@link KvStore}. One lock guards every call, so a commit
 * is atomic with respect to all readers and writers that go through this wrapper.
 */
public final class TxKvStore<K, V> implements KvStore<K, V> {
  private final KvStore<K, V> base;
  private final ReentrantLock lock = new ReentrantLock();

  public TxKvStore(KvStore<K, V> base) {
    this.base = base;
  }

  public Transaction<K, V> begin() {
    return new Transaction<>(this);
  }

  void apply(Map<K, Write<V>> writes) {
    lock.lock();
    try {
      for (Map.Entry<K, Write<V>> en : writes.entrySet()) {
        switch (en.getValue()) {
          case Write.Put<V> p -> {
            if (p.ttl() == null) {
              base.put(en.getKey(), p.value());
            } else {
              base.put(en.getKey(), p.value(), p.ttl());
            }
          }
          case Write.Delete<V> d -> base.delete(en.getKey());
        }
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<V> get(K key) {
    lock.lock();
    try {
      return base.get(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<V> put(K key, V value) {
    lock.lock();
    try {
      return base.put(key, value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<V> put(K key, V value, Duration ttl) {
    lock.lock();
    try {
      return base.put(key, value, ttl);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean putIfAbsent(K key, V value) {
    lock.lock();
    try {
      return base.putIfAbsent(key, value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean compareAndSet(K key, V expected, V update) {
    lock.lock();
    try {
      return base.compareAndSet(key, expected, update);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean delete(K key) {
    lock.lock();
    try {
      return base.delete(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean containsKey(K key) {
    lock.lock();
    try {
      return base.containsKey(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int size() {
    lock.lock();
    try {
      return base.size();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Set<K> keys() {
    lock.lock();
    try {
      return base.keys();
    } finally {
      lock.unlock();
    }
  }
}
// ANCHOR_END: tx-kv-store
