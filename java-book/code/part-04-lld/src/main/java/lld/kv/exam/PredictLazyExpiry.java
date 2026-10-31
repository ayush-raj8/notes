package lld.kv.exam;

import java.time.Duration;
import lld.kv.ManualClock;
import lld.kv.MapKvStore;

// ANCHOR: predict-lazy-expiry
public class PredictLazyExpiry {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    MapKvStore<String, String> kv = new MapKvStore<>(clock);
    kv.put("a", "1", Duration.ofSeconds(5));
    kv.put("b", "2", Duration.ofSeconds(5));
    kv.put("c", "3");
    clock.advance(Duration.ofSeconds(5));
    System.out.println(kv.size() + " " + kv.storedCount());
    System.out.println(kv.containsKey("a"));
    System.out.println(kv.size() + " " + kv.storedCount());
    System.out.println(kv.purgeExpired());
    System.out.println(kv.size() + " " + kv.storedCount());
  }
}
// ANCHOR_END: predict-lazy-expiry
