package lld.ratelimit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

// ANCHOR: sliding-log
/**
 * Keeps the timestamps of accepted requests in the last {@code window} ms. Exact (no boundary
 * burst) but O(limit) memory per key.
 */
public final class SlidingWindowLogLimiter implements RateLimiter {
  private final int limit;
  private final long windowMillis;
  private final LongSupplier clock;
  private final Map<String, Deque<Long>> logs = new ConcurrentHashMap<>();

  public SlidingWindowLogLimiter(int limit, long windowMillis, LongSupplier clock) {
    if (limit <= 0 || windowMillis <= 0) {
      throw new IllegalArgumentException("limit and window must be positive");
    }
    this.limit = limit;
    this.windowMillis = windowMillis;
    this.clock = clock;
  }

  @Override
  public boolean tryAcquire(String key) {
    long now = clock.getAsLong();
    Deque<Long> log = logs.computeIfAbsent(key, k -> new ArrayDeque<>());
    synchronized (log) {
      while (!log.isEmpty() && log.peekFirst() <= now - windowMillis) {
        log.pollFirst();
      }
      if (log.size() < limit) {
        log.addLast(now);
        return true;
      }
      return false;
    }
  }
}
// ANCHOR_END: sliding-log
