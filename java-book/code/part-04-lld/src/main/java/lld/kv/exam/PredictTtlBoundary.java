package lld.kv.exam;

import java.time.Duration;
import lld.kv.ManualClock;
import lld.kv.MapKvStore;

// ANCHOR: predict-ttl-boundary
public class PredictTtlBoundary {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    MapKvStore<String, String> kv = new MapKvStore<>(clock);
    kv.put("k", "v", Duration.ofSeconds(10));
    clock.advance(Duration.ofSeconds(9));
    System.out.println(kv.get("k"));
    clock.advance(Duration.ofSeconds(1));
    System.out.println(kv.get("k"));
    kv.put("k", "v2", Duration.ofSeconds(10));
    clock.advance(Duration.ofSeconds(5));
    System.out.println(kv.compareAndSet("k", "v2", "v3"));
    clock.advance(Duration.ofSeconds(5));
    System.out.println(kv.get("k"));
  }
}
// ANCHOR_END: predict-ttl-boundary
