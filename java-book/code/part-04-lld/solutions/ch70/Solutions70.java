import java.time.Duration;
import java.time.Instant;
import java.util.*;
import lld.scheduler.*;

/** Exercise solutions for chapter 70. */
public class Solutions70 {
  /** Ex 1: convenience for "every N seconds, starting now". */
  static ScheduledTask every(Scheduler s, String name, long seconds, Job job) {
    return s.schedule(name, job, Schedule.fixedRate(Duration.ZERO, Duration.ofSeconds(seconds)));
  }

  /** Ex 2: simulated-time driver: jump to each due time and run, until nothing is due before {@code end}. */
  static int runUntil(Scheduler s, ManualClock clock, Instant end) {
    int runs = 0;
    while (s.nextRunAt().isPresent() && !s.nextRunAt().get().isAfter(end)) {
      clock.advanceTo(s.nextRunAt().get());
      runs += s.runDue().size();
    }
    return runs;
  }

  /** Ex 3: run jobs in order; the first failure stops the chain and propagates. */
  static Job sequence(Job... jobs) {
    return () -> {
      for (Job j : jobs) {
        j.run();
      }
    };
  }

  /** Ex 4: exhaustive description with a record pattern and no default. */
  static String describe(Schedule s) {
    return switch (s) {
      case Schedule.Once(Duration d) -> "once after " + d.toSeconds() + "s";
      case Schedule.FixedRate(Duration i, Duration p) -> "every " + p.toSeconds() + "s from +" + i.toSeconds() + "s (rate)";
      case Schedule.FixedDelay(Duration i, Duration d) -> "every " + d.toSeconds() + "s after each run (delay)";
    };
  }

  /** Ex 5: tasks per status, every status present (zero counts included). */
  static Map<TaskStatus, Integer> countByStatus(Collection<ScheduledTask> tasks) {
    Map<TaskStatus, Integer> out = new EnumMap<>(TaskStatus.class);
    for (TaskStatus st : TaskStatus.values()) {
      out.put(st, 0);
    }
    for (ScheduledTask t : tasks) {
      out.merge(t.status(), 1, Integer::sum);
    }
    return out;
  }

  /** Ex 6: a job that fails its first {@code n} calls, then succeeds. */
  static Job flaky(int n, List<String> log) {
    int[] calls = {0};
    return () -> {
      calls[0]++;
      log.add("call " + calls[0]);
      if (calls[0] <= n) {
        throw new Exception("not yet");
      }
    };
  }

  public static void main(String[] args) throws Exception {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    List<String> log = new ArrayList<>();
    every(s, "ping", 3, () -> log.add("ping@" + clock.seconds()));
    System.out.println("ex1 runs=" + runUntil(s, clock, Instant.EPOCH.plusSeconds(10)) + " " + log);

    List<String> seq = new ArrayList<>();
    try {
      sequence(() -> seq.add("a"), () -> { throw new Exception("stop"); }, () -> seq.add("c")).run();
    } catch (Exception e) {
      seq.add("caught " + e.getMessage());
    }
    System.out.println("ex3 " + seq);

    System.out.println("ex4 " + describe(Schedule.once(Duration.ofSeconds(5))));
    System.out.println("ex4 " + describe(Schedule.fixedRate(Duration.ofSeconds(1), Duration.ofSeconds(4))));
    System.out.println("ex4 " + describe(Schedule.fixedDelay(Duration.ZERO, Duration.ofSeconds(9))));

    ManualClock c2 = new ManualClock();
    Scheduler s2 = new Scheduler(c2);
    List<String> calls = new ArrayList<>();
    List<ScheduledTask> all = new ArrayList<>();
    all.add(s2.schedule("flaky", flaky(2, calls), Schedule.once(Duration.ZERO), 0, RetryPolicy.of(5, Duration.ofSeconds(1))));
    all.add(s2.schedule("dead", flaky(9, new ArrayList<>()), Schedule.once(Duration.ZERO)));
    ScheduledTask later = s2.schedule("later", () -> {}, Schedule.once(Duration.ofHours(1)));
    all.add(later);
    ScheduledTask gone = s2.schedule("gone", () -> {}, Schedule.once(Duration.ofHours(1)));
    s2.cancel(gone.id());
    all.add(gone);
    runUntil(s2, c2, Instant.EPOCH.plusSeconds(60));
    System.out.println("ex6 " + calls);
    System.out.println("ex5 " + countByStatus(all));
  }
}
