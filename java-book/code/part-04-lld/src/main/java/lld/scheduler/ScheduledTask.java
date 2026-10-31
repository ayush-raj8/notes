package lld.scheduler;

import java.time.Instant;

// ANCHOR: scheduled-task
/**
 * The entity. Read access is public; every mutation is package-private so only {@link Scheduler}
 * (holding its lock) can change state.
 */
public final class ScheduledTask {
  private final String id;
  private final String name;
  private final Job job;
  private final Schedule schedule;
  private final int priority;
  private final RetryPolicy retry;
  private final long seq;

  private TaskStatus status = TaskStatus.SCHEDULED;
  private Instant nominal; // the logical start time of the current run (not moved by retries)
  private Instant nextRun;
  private int runCount;
  private int failures;
  private Exception lastError;

  ScheduledTask(
      String id, String name, Job job, Schedule schedule, int priority, RetryPolicy retry, long seq, Instant first) {
    this.id = id;
    this.name = name;
    this.job = job;
    this.schedule = schedule;
    this.priority = priority;
    this.retry = retry;
    this.seq = seq;
    this.nominal = first;
    this.nextRun = first;
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public int priority() {
    return priority;
  }

  public TaskStatus status() {
    return status;
  }

  public Instant nextRun() {
    return nextRun;
  }

  public int runCount() {
    return runCount;
  }

  public Exception lastError() {
    return lastError;
  }

  Job job() {
    return job;
  }

  Schedule schedule() {
    return schedule;
  }

  RetryPolicy retry() {
    return retry;
  }

  long seq() {
    return seq;
  }

  Instant nominal() {
    return nominal;
  }

  int failures() {
    return failures;
  }

  void status(TaskStatus s) {
    this.status = s;
  }

  void startRun() {
    status = TaskStatus.RUNNING;
    runCount++;
  }

  void succeeded(Instant nextNominal) {
    failures = 0;
    lastError = null;
    if (nextNominal == null) {
      status = TaskStatus.COMPLETED;
    } else {
      nominal = nextNominal;
      nextRun = nextNominal;
      status = TaskStatus.SCHEDULED;
    }
  }

  void failedWillRetry(Exception e, Instant retryAt) {
    failures++;
    lastError = e;
    nextRun = retryAt;
    status = TaskStatus.SCHEDULED;
  }

  void failedForGood(Exception e) {
    failures++;
    lastError = e;
    status = TaskStatus.FAILED;
  }
}
// ANCHOR_END: scheduled-task
