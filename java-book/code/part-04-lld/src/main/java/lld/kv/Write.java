package lld.kv;

import java.time.Duration;

// ANCHOR: write
/** A buffered change inside a transaction. Sealed, so the commit switch needs no default. */
sealed interface Write<V> permits Write.Put, Write.Delete {
  record Put<V>(V value, Duration ttl) implements Write<V> {}

  record Delete<V>() implements Write<V> {}
}
// ANCHOR_END: write
