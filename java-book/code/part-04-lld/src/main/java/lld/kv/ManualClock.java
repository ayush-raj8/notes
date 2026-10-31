package lld.kv;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

// ANCHOR: manual-clock
/** A clock that only moves when told to, so TTL behaviour is deterministic in tests and demos. */
public final class ManualClock extends Clock {
  private Instant now;

  public ManualClock() {
    this(Instant.EPOCH);
  }

  public ManualClock(Instant start) {
    this.now = start;
  }

  public synchronized void advance(Duration d) {
    if (d.isNegative()) {
      throw new IllegalArgumentException("time only moves forward: " + d);
    }
    now = now.plus(d);
  }

  @Override
  public synchronized Instant instant() {
    return now;
  }

  @Override
  public ZoneId getZone() {
    return ZoneOffset.UTC;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return this;
  }
}
// ANCHOR_END: manual-clock
