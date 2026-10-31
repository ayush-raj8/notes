package lld.scheduler.exam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lld.scheduler.Job;
import lld.scheduler.ManualClock;
import lld.scheduler.Schedule;
import lld.scheduler.Scheduler;

// ANCHOR: predict-rate-vs-delay
public class PredictRateVsDelay {
  static List<Long> starts(boolean fixedRate) {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    List<Long> starts = new ArrayList<>();
    Job job =
        () -> {
          starts.add(clock.seconds());
          clock.advance(Duration.ofSeconds(3)); // the job takes 3 s
        };
    Duration two = Duration.ofSeconds(2);
    s.schedule("t", job, fixedRate ? Schedule.fixedRate(Duration.ZERO, two) : Schedule.fixedDelay(Duration.ZERO, two));
    while (s.nextRunAt().isPresent() && clock.seconds() <= 10) {
      clock.advanceTo(s.nextRunAt().get());
      s.runDue();
    }
    return starts;
  }

  public static void main(String[] args) {
    System.out.println("rate  " + starts(true));
    System.out.println("delay " + starts(false));
  }
}
// ANCHOR_END: predict-rate-vs-delay
