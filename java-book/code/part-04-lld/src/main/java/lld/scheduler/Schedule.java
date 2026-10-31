package lld.scheduler;

import java.time.Duration;

// ANCHOR: schedule
/** When a task runs. Sealed: the scheduler's switch over it needs no {@code default}. */
public sealed interface Schedule permits Schedule.Once, Schedule.FixedRate, Schedule.FixedDelay {
  /** Run once, {@code delay} after scheduling. */
  record Once(Duration delay) implements Schedule {
    public Once {
      requireNotNegative(delay, "delay");
    }
  }

  /** Start times are {@code initialDelay + n * period}, whatever the run duration (can pile up). */
  record FixedRate(Duration initialDelay, Duration period) implements Schedule {
    public FixedRate {
      requireNotNegative(initialDelay, "initialDelay");
      requirePositive(period, "period");
    }
  }

  /** The next start is {@code delay} after the previous run <em>finished</em> (never overlaps). */
  record FixedDelay(Duration initialDelay, Duration delay) implements Schedule {
    public FixedDelay {
      requireNotNegative(initialDelay, "initialDelay");
      requirePositive(delay, "delay");
    }
  }

  static Schedule once(Duration delay) {
    return new Once(delay);
  }

  static Schedule fixedRate(Duration initialDelay, Duration period) {
    return new FixedRate(initialDelay, period);
  }

  static Schedule fixedDelay(Duration initialDelay, Duration delay) {
    return new FixedDelay(initialDelay, delay);
  }

  default Duration firstDelay() {
    return switch (this) {
      case Once o -> o.delay();
      case FixedRate r -> r.initialDelay();
      case FixedDelay d -> d.initialDelay();
    };
  }

  private static void requireNotNegative(Duration d, String what) {
    if (d.isNegative()) {
      throw new IllegalArgumentException(what + " must not be negative: " + d);
    }
  }

  private static void requirePositive(Duration d, String what) {
    if (d.isNegative() || d.isZero()) {
      throw new IllegalArgumentException(what + " must be positive: " + d);
    }
  }
}
// ANCHOR_END: schedule
