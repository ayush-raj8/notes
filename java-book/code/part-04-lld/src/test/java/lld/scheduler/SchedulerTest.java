package lld.scheduler;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchedulerTest {
  private ManualClock clock;
  private Scheduler s;
  private List<String> log;

  @BeforeEach
  void setUp() {
    clock = new ManualClock();
    s = new Scheduler(clock);
    log = new ArrayList<>();
  }

  private static Duration secs(long n) {
    return Duration.ofSeconds(n);
  }

  private Job logs(String what) {
    return () -> log.add(what);
  }

  @Test
  void nothingRunsBeforeItsTime() {
    s.schedule("a", logs("a"), Schedule.once(secs(5)));
    clock.advance(secs(4));
    assertEquals(List.of(), s.runDue());
    clock.advance(secs(1));
    assertEquals(List.of("a"), s.runDue());
    assertEquals(List.of(), s.runDue());
  }

  @Test
  void orderIsTimeThenPriorityThenSubmission() {
    Schedule at5 = Schedule.once(secs(5));
    s.schedule("A", logs("A"), at5, 1, RetryPolicy.none());
    s.schedule("B", logs("B"), at5, 5, RetryPolicy.none());
    s.schedule("C", logs("C"), Schedule.once(secs(3)), 0, RetryPolicy.none());
    s.schedule("D", logs("D"), at5, 5, RetryPolicy.none());
    clock.advance(secs(5));
    assertEquals(List.of("C", "B", "D", "A"), s.runDue());
  }

  @Test
  void validation() {
    assertThrows(IllegalArgumentException.class, () -> s.schedule(" ", logs("x"), Schedule.once(secs(1))));
    assertThrows(NullPointerException.class, () -> s.schedule("n", null, Schedule.once(secs(1))));
    assertThrows(IllegalArgumentException.class, () -> Schedule.once(secs(-1)));
    assertThrows(IllegalArgumentException.class, () -> Schedule.fixedRate(secs(0), secs(0)));
    assertThrows(IllegalArgumentException.class, () -> Schedule.fixedDelay(secs(0), secs(-2)));
    assertThrows(IllegalArgumentException.class, () -> RetryPolicy.of(0, secs(1)));
    assertThrows(IllegalArgumentException.class, () -> RetryPolicy.of(3, Duration.ZERO));
  }

  @Test
  void onceCompletesAndIsNotRequeued() {
    ScheduledTask t = s.schedule("a", logs("a"), Schedule.once(Duration.ZERO));
    s.runDue();
    assertEquals(TaskStatus.COMPLETED, t.status());
    assertEquals(1, t.runCount());
    assertEquals(0, s.pendingCount());
  }

  @Test
  void fixedRateKeepsItsGridAndCatchesUp() {
    s.schedule("r", logs("r"), Schedule.fixedRate(Duration.ZERO, secs(2)));
    clock.advance(secs(10));
    assertEquals(6, s.runDue().size()); // 0,2,4,6,8,10
    assertEquals(Instant.EPOCH.plusSeconds(12), s.nextRunAt().orElseThrow());
  }

  @Test
  void fixedDelayCountsFromTheEndOfTheRun() {
    s.schedule("d", logs("d"), Schedule.fixedDelay(Duration.ZERO, secs(2)));
    clock.advance(secs(10));
    assertEquals(1, s.runDue().size());
    assertEquals(Instant.EPOCH.plusSeconds(12), s.nextRunAt().orElseThrow());
  }

  @Test
  void aJobThatMovesTheClockCannotMakeRunDueLoopForever() {
    s.schedule("slow", () -> clock.advance(secs(5)), Schedule.fixedRate(Duration.ZERO, secs(1)));
    assertEquals(1, s.runDue().size()); // snapshot of "now" taken at the start
  }

  @Test
  void cancelRemovesAScheduledTask() {
    ScheduledTask t = s.schedule("a", logs("a"), Schedule.once(secs(1)));
    assertTrue(s.cancel(t.id()));
    assertFalse(s.cancel(t.id()));
    assertFalse(s.cancel("missing"));
    clock.advance(secs(5));
    assertEquals(List.of(), s.runDue());
    assertEquals(TaskStatus.CANCELLED, t.status());
    assertEquals(0, s.pendingCount());
  }

  @Test
  void cancelWhileRunningStopsAfterTheCurrentRun() {
    ScheduledTask[] self = new ScheduledTask[1];
    self[0] = s.schedule("tick", () -> s.cancel(self[0].id()), Schedule.fixedRate(Duration.ZERO, secs(1)));
    clock.advance(secs(5));
    assertEquals(List.of("tick"), s.runDue());
    assertEquals(TaskStatus.CANCELLED, self[0].status());
    assertEquals(0, s.pendingCount());
  }

  @Test
  void finishedTasksCannotBeCancelled() {
    ScheduledTask t = s.schedule("a", logs("a"), Schedule.once(Duration.ZERO));
    s.runDue();
    assertFalse(s.cancel(t.id()));
    assertEquals(TaskStatus.COMPLETED, t.status());
  }

  @Test
  void aFailingJobDoesNotStopTheOthers() {
    Schedule now = Schedule.once(Duration.ZERO);
    ScheduledTask bad = s.schedule("bad", () -> { throw new Exception("checked"); }, now);
    ScheduledTask npe = s.schedule("npe", () -> { throw new NullPointerException("x"); }, now);
    s.schedule("ok", logs("ok"), now);
    assertEquals(List.of("bad", "npe", "ok"), s.runDue());
    assertEquals(List.of("ok"), log);
    assertEquals(TaskStatus.FAILED, bad.status());
    assertEquals(TaskStatus.FAILED, npe.status());
    assertEquals("checked", bad.lastError().getMessage());
  }

  @Test
  void retriesBackOffExponentiallyThenFail() {
    List<Long> at = new ArrayList<>();
    ScheduledTask t =
        s.schedule(
            "r",
            () -> {
              at.add(clock.seconds());
              throw new Exception("no");
            },
            Schedule.once(Duration.ZERO),
            0,
            RetryPolicy.of(3, secs(2)));
    while (s.nextRunAt().isPresent()) {
      clock.advanceTo(s.nextRunAt().orElseThrow());
      s.runDue();
    }
    assertEquals(List.of(0L, 2L, 6L), at);
    assertEquals(TaskStatus.FAILED, t.status());
    assertEquals(3, t.runCount());
  }

  @Test
  void aSuccessfulRetryCompletesTheTaskAndClearsTheError() {
    int[] n = {0};
    ScheduledTask t =
        s.schedule(
            "r",
            () -> {
              if (++n[0] < 3) {
                throw new IllegalStateException("flaky");
              }
            },
            Schedule.once(Duration.ZERO),
            0,
            RetryPolicy.of(5, secs(1)));
    while (s.nextRunAt().isPresent()) {
      clock.advanceTo(s.nextRunAt().orElseThrow());
      s.runDue();
    }
    assertEquals(TaskStatus.COMPLETED, t.status());
    assertEquals(3, t.runCount());
    assertNull(t.lastError());
  }

  @Test
  void retryDoesNotShiftTheFixedRateGrid() {
    int[] n = {0};
    ScheduledTask t =
        s.schedule(
            "r",
            () -> {
              if (++n[0] == 1) {
                throw new Exception("first attempt fails");
              }
            },
            Schedule.fixedRate(Duration.ZERO, secs(10)),
            0,
            RetryPolicy.of(2, secs(3)));
    s.runDue(); // fails at 0, retry at 3
    clock.advance(secs(3));
    s.runDue(); // succeeds; next nominal slot is 10, not 13
    assertEquals(Instant.EPOCH.plusSeconds(10), t.nextRun());
  }

  @Test
  void jobsCanScheduleMoreWork() {
    s.schedule("parent", () -> s.schedule("child", logs("child"), Schedule.once(Duration.ZERO)), Schedule.once(Duration.ZERO));
    assertEquals(List.of("parent", "child"), s.runDue());
  }

  @Test
  void findAndNextRunAt() {
    ScheduledTask t = s.schedule("a", logs("a"), Schedule.once(secs(7)));
    assertEquals(t, s.find(t.id()).orElseThrow());
    assertTrue(s.find("none").isEmpty());
    assertEquals(secs(7), s.untilNext());
  }

  @Test
  void scheduleFromManyThreadsKeepsEveryTask() throws Exception {
    List<Thread> ts = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      Thread t = new Thread(() -> {
        for (int j = 0; j < 100; j++) {
          s.schedule("t", () -> {}, Schedule.once(Duration.ZERO));
        }
      });
      ts.add(t);
      t.start();
    }
    for (Thread t : ts) {
      t.join();
    }
    assertEquals(800, s.pendingCount());
    assertEquals(800, s.runDue().size());
  }

  @Test
  void timerDriverRunsTasksInRealTime() throws Exception {
    Scheduler real = new Scheduler(java.time.Clock.systemUTC());
    CountDownLatch done = new CountDownLatch(3);
    AtomicInteger runs = new AtomicInteger();
    try (TimerDriver driver = new TimerDriver(real, Duration.ofMillis(5))) {
      real.schedule("once", done::countDown, Schedule.once(Duration.ofMillis(10)));
      real.schedule("again", () -> { runs.incrementAndGet(); done.countDown(); }, Schedule.fixedDelay(Duration.ZERO, Duration.ofMillis(10)));
      assertTrue(done.await(5, TimeUnit.SECONDS));
    }
    assertTrue(runs.get() >= 2);
  }

  @Test
  void aThrowingJobDoesNotKillTheTimerLoop() throws Exception {
    Scheduler real = new Scheduler(java.time.Clock.systemUTC());
    CountDownLatch ok = new CountDownLatch(1);
    try (TimerDriver driver = new TimerDriver(real, Duration.ofMillis(5))) {
      real.schedule("bad", () -> { throw new IllegalStateException("x"); }, Schedule.once(Duration.ZERO));
      real.schedule("good", ok::countDown, Schedule.once(Duration.ofMillis(20)));
      assertTrue(ok.await(5, TimeUnit.SECONDS));
    }
  }

  @Test
  void statusEnumKnowsItsTerminalStates() {
    assertFalse(TaskStatus.SCHEDULED.isTerminal());
    assertFalse(TaskStatus.RUNNING.isTerminal());
    assertTrue(TaskStatus.COMPLETED.isTerminal());
    assertTrue(TaskStatus.CANCELLED.isTerminal());
    assertTrue(TaskStatus.FAILED.isTerminal());
  }
}
