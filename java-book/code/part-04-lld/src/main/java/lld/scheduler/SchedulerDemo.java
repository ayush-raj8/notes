package lld.scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// ANCHOR: scheduler-demo
public final class SchedulerDemo {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    List<String> log = new ArrayList<>();

    s.schedule("report", () -> log.add("report@" + clock.seconds()), Schedule.once(Duration.ofSeconds(5)));
    s.schedule("urgent", () -> log.add("urgent@" + clock.seconds()), Schedule.once(Duration.ofSeconds(5)), 9, RetryPolicy.none());
    s.schedule("heartbeat", () -> log.add("beat@" + clock.seconds()), Schedule.fixedRate(Duration.ofSeconds(2), Duration.ofSeconds(4)));
    int[] attempts = {0};
    ScheduledTask flaky =
        s.schedule(
            "flaky",
            () -> {
              attempts[0]++;
              if (attempts[0] < 3) {
                throw new IllegalStateException("try " + attempts[0]);
              }
              log.add("flaky ok@" + clock.seconds());
            },
            Schedule.once(Duration.ofSeconds(1)),
            0,
            RetryPolicy.of(4, Duration.ofSeconds(1)));
    ScheduledTask doomed = s.schedule("doomed", () -> log.add("never"), Schedule.once(Duration.ofSeconds(3)));
    System.out.println("cancel doomed: " + s.cancel(doomed.id()) + " again: " + s.cancel(doomed.id()));

    while (s.nextRunAt().isPresent() && s.nextRunAt().get().isBefore(java.time.Instant.EPOCH.plusSeconds(12))) {
      clock.advanceTo(s.nextRunAt().get());
      s.runDue();
    }
    log.forEach(System.out::println);
    System.out.println("flaky: " + flaky.status() + " after " + flaky.runCount() + " runs");
    System.out.println("doomed: " + doomed.status() + " runs=" + doomed.runCount());
    System.out.println("pending: " + s.pendingCount());
  }
}
// ANCHOR_END: scheduler-demo
