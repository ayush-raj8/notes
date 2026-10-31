package lld.ratelimit;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

// ANCHOR: manual-clock
/** Test clock in milliseconds. Limiters take a {@link LongSupplier}, so production passes System::currentTimeMillis. */
public final class ManualClock implements LongSupplier {
  private final AtomicLong now;

  public ManualClock(long startMillis) {
    this.now = new AtomicLong(startMillis);
  }

  public void advance(long millis) {
    now.addAndGet(millis);
  }

  public void set(long millis) {
    now.set(millis);
  }

  @Override
  public long getAsLong() {
    return now.get();
  }
}
// ANCHOR_END: manual-clock
