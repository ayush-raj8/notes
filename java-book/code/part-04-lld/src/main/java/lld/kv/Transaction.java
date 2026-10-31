package lld.kv;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// ANCHOR: transaction
/**
 * Buffered, all-or-nothing group of changes (read committed, last writer wins on conflict).
 * Reads see this transaction's own writes first. {@link #close()} rolls back an open transaction,
 * so try-with-resources never leaks pending writes.
 */
public final class Transaction<K, V> implements AutoCloseable {
  private final TxKvStore<K, V> store;
  private final Map<K, Write<V>> writes = new LinkedHashMap<>(); // insertion order = apply order
  private boolean open = true;

  Transaction(TxKvStore<K, V> store) {
    this.store = store;
  }

  public Optional<V> get(K key) {
    requireOpen();
    Write<V> w = writes.get(key);
    return switch (w) {
      case null -> store.get(key);
      case Write.Put<V> p -> Optional.of(p.value());
      case Write.Delete<V> d -> Optional.empty();
    };
  }

  public void put(K key, V value) {
    put(key, value, null);
  }

  public void put(K key, V value, Duration ttl) {
    requireOpen();
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(value, "value");
    writes.put(key, new Write.Put<>(value, ttl));
  }

  public void delete(K key) {
    requireOpen();
    writes.put(Objects.requireNonNull(key, "key"), new Write.Delete<>());
  }

  public void commit() {
    requireOpen();
    store.apply(writes);
    open = false;
  }

  public void rollback() {
    requireOpen();
    writes.clear();
    open = false;
  }

  public boolean isOpen() {
    return open;
  }

  public int pendingWrites() {
    return writes.size();
  }

  @Override
  public void close() {
    if (open) {
      rollback();
    }
  }

  private void requireOpen() {
    if (!open) {
      throw new IllegalStateException("transaction already finished");
    }
  }
}
// ANCHOR_END: transaction
