package lld.scheduler.exam;

import java.time.Duration;
import lld.scheduler.ManualClock;
import lld.scheduler.Schedule;
import lld.scheduler.Scheduler;

// ANCHOR: predict-catch-up
public class PredictCatchUp {
  public static void main(String[] args) {
    Duration two = Duration.ofSeconds(2);
    for (boolean rate : new boolean[] {true, false}) {
      ManualClock clock = new ManualClock();
      Scheduler s = new Scheduler(clock);
      s.schedule("job", () -> {}, rate ? Schedule.fixedRate(Duration.ZERO, two) : Schedule.fixedDelay(Duration.ZERO, two));
      clock.advance(Duration.ofSeconds(10)); // the process was paused for 10 s
      System.out.println((rate ? "rate  " : "delay ") + s.runDue().size() + " next=" + s.nextRunAt().get().getEpochSecond());
    }
  }
}
// ANCHOR_END: predict-catch-up
