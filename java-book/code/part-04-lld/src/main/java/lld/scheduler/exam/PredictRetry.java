package lld.scheduler.exam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lld.scheduler.ManualClock;
import lld.scheduler.RetryPolicy;
import lld.scheduler.Schedule;
import lld.scheduler.ScheduledTask;
import lld.scheduler.Scheduler;

// ANCHOR: predict-retry
public class PredictRetry {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    List<Long> attempts = new ArrayList<>();
    ScheduledTask t =
        s.schedule(
            "always-fails",
            () -> {
              attempts.add(clock.seconds());
              throw new Exception("fail #" + attempts.size());
            },
            Schedule.once(Duration.ZERO),
            0,
            RetryPolicy.of(3, Duration.ofSeconds(2)));
    while (s.nextRunAt().isPresent()) {
      clock.advanceTo(s.nextRunAt().get());
      s.runDue();
    }
    System.out.println(attempts);
    System.out.println(t.status() + " " + t.runCount() + " " + t.lastError().getMessage());
    System.out.println(RetryPolicy.of(5, Duration.ofSeconds(1)).delayAfter(4));
  }
}
// ANCHOR_END: predict-retry
