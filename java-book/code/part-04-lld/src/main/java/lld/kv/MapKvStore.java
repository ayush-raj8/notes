package lld.kv;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

// ANCHOR: map-kv-store
/**
 * Thread-safe store backed by a {@link ConcurrentHashMap}. Expiry is lazy: an expired entry is
 * invisible to every read and is physically removed when touched or by {@link #purgeExpired()}.
 * Check-then-act operations use {@code compute*} so the decision and the write are one atomic step.
 */
public final class MapKvStore<K, V> implements KvStore<K, V> {
  private final ConcurrentHashMap<K, Entry<V>> map = new ConcurrentHashMap<>();
  private final Clock clock;

  public MapKvStore(Clock clock) {
    this.clock = Objects.requireNonNull(clock, "clock");
  }

  public MapKvStore() {
    this(Clock.systemUTC());
  }

  @Override
  public Optional<V> get(K key) {
    Objects.requireNonNull(key, "key");
    Entry<V> e = map.get(key);
    if (e == null) {
      return Optional.empty();
    }
    if (e.expiredAt(clock.instant())) {
      map.remove(key, e); // only if nobody replaced it in the meantime
      return Optional.empty();
    }
    return Optional.of(e.value());
  }

  @Override
  public Optional<V> put(K key, V value) {
    return store(key, Entry.forever(value));
  }

  @Override
  public Optional<V> put(K key, V value, Duration ttl) {
    if (ttl.isZero() || ttl.isNegative()) {
      throw new IllegalArgumentException("ttl must be positive: " + ttl);
    }
    return store(key, new Entry<>(value, clock.instant().plus(ttl)));
  }

  private Optional<V> store(K key, Entry<V> fresh) {
    Objects.requireNonNull(key, "key");
    Entry<V> previous = map.put(key, fresh);
    return live(previous);
  }

  @Override
  public boolean putIfAbsent(K key, V value) {
    Objects.requireNonNull(key, "key");
    Instant now = clock.instant();
    Entry<V> fresh = Entry.forever(value);
    Entry<V> result = map.compute(key, (k, old) -> old == null || old.expiredAt(now) ? fresh : old);
    return result == fresh;
  }

  @Override
  public boolean compareAndSet(K key, V expected, V update) {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(update, "update");
    Instant now = clock.instant();
    AtomicBoolean swapped = new AtomicBoolean(); // lambdas may not assign a local boolean
    map.computeIfPresent(
        key,
        (k, old) -> {
          if (old.expiredAt(now)) {
            return null; // drop it: expired means absent
          }
          if (old.value().equals(expected)) {
            swapped.set(true);
            return new Entry<>(update, old.expiresAt());
          }
          return old;
        });
    return swapped.get();
  }

  @Override
  public boolean delete(K key) {
    Objects.requireNonNull(key, "key");
    return live(map.remove(key)).isPresent();
  }

  @Override
  public boolean containsKey(K key) {
    return get(key).isPresent();
  }

  @Override
  public int size() {
    Instant now = clock.instant();
    return (int) map.values().stream().filter(e -> !e.expiredAt(now)).count();
  }

  /** Entries physically held, expired-but-not-yet-purged included. */
  public int storedCount() {
    return map.size();
  }

  /** Removes every expired entry; returns how many were removed. */
  public int purgeExpired() {
    Instant now = clock.instant();
    int before = map.size();
    map.entrySet().removeIf(en -> en.getValue().expiredAt(now));
    return before - map.size();
  }

  @Override
  public Set<K> keys() {
    Instant now = clock.instant();
    Set<K> out = new HashSet<>();
    for (Map.Entry<K, Entry<V>> en : map.entrySet()) {
      if (!en.getValue().expiredAt(now)) {
        out.add(en.getKey());
      }
    }
    return Collections.unmodifiableSet(out);
  }

  private Optional<V> live(Entry<V> e) {
    return e == null || e.expiredAt(clock.instant()) ? Optional.empty() : Optional.of(e.value());
  }
}
// ANCHOR_END: map-kv-store
