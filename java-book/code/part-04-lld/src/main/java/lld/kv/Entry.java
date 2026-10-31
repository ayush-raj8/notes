package lld.kv;

import java.time.Instant;
import java.util.Objects;

// ANCHOR: entry
/** One stored value plus its optional expiry instant ({@code null} = never expires). */
public record Entry<V>(V value, Instant expiresAt) {
  public Entry {
    Objects.requireNonNull(value, "value");
  }

  public static <V> Entry<V> forever(V value) {
    return new Entry<>(value, null);
  }

  /** Expired exactly at {@code expiresAt}: the boundary instant is already gone. */
  public boolean expiredAt(Instant now) {
    return expiresAt != null && !now.isBefore(expiresAt);
  }
}
// ANCHOR_END: entry
