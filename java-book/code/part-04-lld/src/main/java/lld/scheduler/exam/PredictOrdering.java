package lld.scheduler.exam;

import java.time.Duration;
import lld.scheduler.ManualClock;
import lld.scheduler.RetryPolicy;
import lld.scheduler.Schedule;
import lld.scheduler.Scheduler;

// ANCHOR: predict-ordering
public class PredictOrdering {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    Schedule at5 = Schedule.once(Duration.ofSeconds(5));
    s.schedule("A", () -> {}, at5, 1, RetryPolicy.none());
    s.schedule("B", () -> {}, at5, 5, RetryPolicy.none());
    s.schedule("C", () -> {}, Schedule.once(Duration.ofSeconds(3)), 0, RetryPolicy.none());
    s.schedule("D", () -> {}, at5, 5, RetryPolicy.none());
    clock.advance(Duration.ofSeconds(4));
    System.out.println(s.runDue());
    clock.advance(Duration.ofSeconds(1));
    System.out.println(s.runDue());
    System.out.println(s.runDue());
  }
}
// ANCHOR_END: predict-ordering
