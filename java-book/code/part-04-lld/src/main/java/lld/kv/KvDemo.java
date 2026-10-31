package lld.kv;

import java.time.Duration;

// ANCHOR: kv-demo
public final class KvDemo {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    MapKvStore<String, String> raw = new MapKvStore<>(clock);
    TxKvStore<String, String> kv = new TxKvStore<>(raw);

    System.out.println(kv.put("user:1", "asha"));
    System.out.println(kv.put("user:1", "asha-v2"));
    kv.put("session:9", "token", Duration.ofSeconds(30));
    System.out.println(kv.get("session:9") + " " + kv.size());

    clock.advance(Duration.ofSeconds(30));
    System.out.println("live=" + kv.size() + " stored=" + raw.storedCount());
    System.out.println("purged " + raw.purgeExpired() + ", stored=" + raw.storedCount());
    System.out.println(kv.get("session:9"));

    System.out.println(kv.putIfAbsent("user:1", "other") + " " + kv.putIfAbsent("user:2", "ravi"));
    System.out.println(kv.compareAndSet("user:1", "asha-v2", "asha-v3") + " " + kv.getOrDefault("user:1", "?"));

    try (Transaction<String, String> tx = kv.begin()) {
      tx.put("user:3", "mira");
      tx.delete("user:2");
      System.out.println("inside tx: " + tx.get("user:3") + " " + tx.get("user:2") + " outside: " + kv.get("user:3"));
      tx.commit();
    }
    try (Transaction<String, String> tx = kv.begin()) {
      tx.put("user:4", "never committed");
    }
    System.out.println(KvStore.keysWithPrefix(kv, "user:"));
  }
}
// ANCHOR_END: kv-demo
