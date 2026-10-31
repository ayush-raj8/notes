package lld.scheduler.exam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lld.scheduler.ManualClock;
import lld.scheduler.Schedule;
import lld.scheduler.ScheduledTask;
import lld.scheduler.Scheduler;

// ANCHOR: predict-failure-isolation
public class PredictFailureIsolation {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock();
    Scheduler s = new Scheduler(clock);
    List<String> log = new ArrayList<>();
    Schedule now = Schedule.once(Duration.ZERO);
    ScheduledTask a = s.schedule("a", () -> log.add("a"), now);
    ScheduledTask bad = s.schedule("bad", () -> { throw new Exception("checked"); }, now);
    ScheduledTask npe = s.schedule("npe", () -> { throw new NullPointerException("unchecked"); }, now);
    ScheduledTask c = s.schedule("c", () -> log.add("c"), now);
    System.out.println(s.runDue());
    System.out.println(log);
    System.out.println(a.status() + " " + bad.status() + " " + npe.status() + " " + c.status());
    System.out.println(bad.lastError().getMessage() + " / " + npe.lastError().getClass().getSimpleName());
    System.out.println(s.pendingCount());
  }
}
// ANCHOR_END: predict-failure-isolation
