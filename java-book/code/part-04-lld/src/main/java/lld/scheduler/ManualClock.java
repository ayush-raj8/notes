package lld.scheduler;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

// ANCHOR: manual-clock
/** A clock that only moves when told to; jobs may advance it to simulate how long they take. */
public final class ManualClock extends Clock {
  private Instant now;

  public ManualClock() {
    this.now = Instant.EPOCH;
  }

  public synchronized void advance(Duration d) {
    if (d.isNegative()) {
      throw new IllegalArgumentException("time only moves forward: " + d);
    }
    now = now.plus(d);
  }

  /** Moves forward to {@code t}; does nothing if the clock is already at or past it. */
  public synchronized void advanceTo(Instant t) {
    if (t.isAfter(now)) {
      now = t;
    }
  }

  /** Whole seconds since the epoch: handy for compact logs. */
  public long seconds() {
    return Duration.between(Instant.EPOCH, instant()).toSeconds();
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
