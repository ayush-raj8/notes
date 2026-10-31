package lld.scheduler;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;

// ANCHOR: scheduler
/**
 * Deterministic scheduler core: a {@link PriorityQueue} ordered by (due time, higher priority first,
 * submission order). {@link #runDue()} executes everything due at the instant it started; the real-time
 * loop is a separate class ({@link TimerDriver}). Jobs run <em>outside</em> the lock, so a job may
 * schedule or cancel tasks, and a slow job never blocks {@code schedule}/{@code cancel} callers.
 */
public final class Scheduler {
  private static final Comparator<ScheduledTask> ORDER =
      Comparator.comparing(ScheduledTask::nextRun)
          .thenComparing(Comparator.comparingInt(ScheduledTask::priority).reversed())
          .thenComparingLong(ScheduledTask::seq);

  private final Clock clock;
  private final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>(ORDER);
  private final Map<String, ScheduledTask> byId = new HashMap<>();
  private final Object lock = new Object();
  private long seq;

  public Scheduler(Clock clock) {
    this.clock = Objects.requireNonNull(clock, "clock");
  }

  public ScheduledTask schedule(String name, Job job, Schedule schedule) {
    return schedule(name, job, schedule, 0, RetryPolicy.none());
  }

  public ScheduledTask schedule(String name, Job job, Schedule schedule, int priority, RetryPolicy retry) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    Objects.requireNonNull(job, "job");
    Objects.requireNonNull(schedule, "schedule");
    Objects.requireNonNull(retry, "retry");
    synchronized (lock) {
      long n = ++seq;
      ScheduledTask t =
          new ScheduledTask("t" + n, name, job, schedule, priority, retry, n, clock.instant().plus(schedule.firstDelay()));
      byId.put(t.id(), t);
      queue.add(t);
      return t;
    }
  }

  /** True when the task was still live. A running task finishes its current run, then stops. */
  public boolean cancel(String id) {
    synchronized (lock) {
      ScheduledTask t = byId.get(id);
      if (t == null || t.status().isTerminal()) {
        return false;
      }
      if (t.status() == TaskStatus.SCHEDULED) {
        queue.remove(t);
      }
      t.status(TaskStatus.CANCELLED);
      return true;
    }
  }

  /** Runs every task due at the instant this call began; returns task names in execution order. */
  public List<String> runDue() {
    Instant now = clock.instant(); // snapshot: a job that moves the clock cannot make this loop endless
    List<String> ran = new ArrayList<>();
    while (true) {
      ScheduledTask t;
      synchronized (lock) {
        t = queue.peek();
        if (t == null || t.nextRun().isAfter(now)) {
          return ran;
        }
        queue.poll();
        t.startRun();
      }
      Exception error = null;
      try {
        t.job().run();
      } catch (Exception e) {
        error = e;
      }
      synchronized (lock) {
        ran.add(t.name());
        finish(t, error);
      }
    }
  }

  // caller holds the lock
  private void finish(ScheduledTask t, Exception error) {
    if (t.status() == TaskStatus.CANCELLED) {
      return; // cancelled while running: do not requeue
    }
    if (error == null) {
      t.succeeded(nextNominal(t));
    } else if (t.retry().allowsRetryAfter(t.failures() + 1)) {
      t.failedWillRetry(error, clock.instant().plus(t.retry().delayAfter(t.failures() + 1)));
    } else {
      t.failedForGood(error);
    }
    if (t.status() == TaskStatus.SCHEDULED) {
      queue.add(t);
    }
  }

  /** Nominal start of the next run, or {@code null} when the task is finished. */
  private Instant nextNominal(ScheduledTask t) {
    return switch (t.schedule()) {
      case Schedule.Once o -> null;
      case Schedule.FixedRate r -> t.nominal().plus(r.period());
      case Schedule.FixedDelay d -> clock.instant().plus(d.delay());
    };
  }

  public Optional<Instant> nextRunAt() {
    synchronized (lock) {
      return Optional.ofNullable(queue.peek()).map(ScheduledTask::nextRun);
    }
  }

  public int pendingCount() {
    synchronized (lock) {
      return queue.size();
    }
  }

  public Optional<ScheduledTask> find(String id) {
    synchronized (lock) {
      return Optional.ofNullable(byId.get(id));
    }
  }

  public Duration untilNext() {
    return nextRunAt().map(n -> Duration.between(clock.instant(), n)).orElse(Duration.ZERO);
  }
}
// ANCHOR_END: scheduler
