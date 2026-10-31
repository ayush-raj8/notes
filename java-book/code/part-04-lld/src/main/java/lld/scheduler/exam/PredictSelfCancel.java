package lld.scheduler.exam;

import java.time.Duration;
import lld.scheduler.ManualClock;
import lld.scheduler.Schedule;
import lld.scheduler.ScheduledTask;
import lld.scheduler.Scheduler;

// ANCHOR: predict-self-cancel
public class PredictSelfCancel {
  static ScheduledTask self;

  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    int[] runs = {0};
    self =
        s.schedule(
            "tick",
            () -> {
              runs[0]++;
              if (runs[0] == 3) {
                System.out.println("cancel from inside: " + s.cancel(self.id()));
              }
            },
            Schedule.fixedRate(Duration.ZERO, Duration.ofSeconds(1)));
    for (int i = 0; i < 6; i++) {
      s.runDue();
      clock.advance(Duration.ofSeconds(1));
    }
    System.out.println(runs[0] + " " + self.status() + " " + s.pendingCount());
    System.out.println(s.cancel(self.id()) + " " + s.cancel("t99"));
  }
}
// ANCHOR_END: predict-self-cancel
