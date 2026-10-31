package lld.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

// ANCHOR: token-bucket
/**
 * Bucket of {@code capacity} tokens, refilled by {@code refillTokens} every {@code perMillis}.
 * A request costs one token. Stored in integer "units" (tokens x perMillis) so no rounding creeps in.
 * Allows bursts up to capacity, then the steady refill rate.
 */
public final class TokenBucketLimiter implements RateLimiter {
  private static final class Bucket {
    private long units;
    private long lastMillis;

    Bucket(long units, long lastMillis) {
      this.units = units;
      this.lastMillis = lastMillis;
    }
  }

  private final long capacityUnits;
  private final long refillUnitsPerMilli;
  private final long costUnits;
  private final LongSupplier clock;
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  public TokenBucketLimiter(int capacity, int refillTokens, long perMillis, LongSupplier clock) {
    if (capacity <= 0 || refillTokens <= 0 || perMillis <= 0) {
      throw new IllegalArgumentException("capacity, refill and period must be positive");
    }
    this.costUnits = perMillis;
    this.capacityUnits = capacity * perMillis;
    this.refillUnitsPerMilli = refillTokens;
    this.clock = clock;
  }

  @Override
  public boolean tryAcquire(String key) {
    long now = clock.getAsLong();
    Bucket b = buckets.computeIfAbsent(key, k -> new Bucket(capacityUnits, now));
    synchronized (b) {
      long elapsed = Math.max(0, now - b.lastMillis);
      b.units = Math.min(capacityUnits, b.units + elapsed * refillUnitsPerMilli);
      b.lastMillis = now;
      if (b.units >= costUnits) {
        b.units -= costUnits;
        return true;
      }
      return false;
    }
  }
}
// ANCHOR_END: token-bucket
