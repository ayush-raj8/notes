package lld.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

// ANCHOR: fixed-window
/**
 * Counts requests per aligned window [k*window, (k+1)*window). Cheap (two longs per key) but a
 * client can send {@code limit} just before and {@code limit} just after a boundary.
 */
public final class FixedWindowLimiter implements RateLimiter {
  private static final class Window {
    private long index = Long.MIN_VALUE;
    private int count;
  }

  private final int limit;
  private final long windowMillis;
  private final LongSupplier clock;
  private final Map<String, Window> windows = new ConcurrentHashMap<>();

  public FixedWindowLimiter(int limit, long windowMillis, LongSupplier clock) {
    if (limit <= 0 || windowMillis <= 0) {
      throw new IllegalArgumentException("limit and window must be positive");
    }
    this.limit = limit;
    this.windowMillis = windowMillis;
    this.clock = clock;
  }

  @Override
  public boolean tryAcquire(String key) {
    long index = Math.floorDiv(clock.getAsLong(), windowMillis);
    Window w = windows.computeIfAbsent(key, k -> new Window());
    synchronized (w) {
      if (w.index != index) {
        w.index = index;
        w.count = 0;
      }
      if (w.count < limit) {
        w.count++;
        return true;
      }
      return false;
    }
  }
}
// ANCHOR_END: fixed-window
