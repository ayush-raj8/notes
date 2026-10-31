package lld.scheduler;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// ANCHOR: timer-driver
/** Real-time loop: one daemon thread calls {@link Scheduler#runDue()} every tick (jobs run serially). */
public final class TimerDriver implements AutoCloseable {
  private final ScheduledExecutorService loop =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "scheduler-loop");
            t.setDaemon(true);
            return t;
          });

  public TimerDriver(Scheduler scheduler, Duration tick) {
    loop.scheduleWithFixedDelay(
        () -> {
          try {
            scheduler.runDue();
          } catch (RuntimeException e) {
            // an unchecked failure must not cancel the periodic loop
          }
        },
        0,
        tick.toMillis(),
        TimeUnit.MILLISECONDS);
  }

  @Override
  public void close() {
    loop.shutdownNow();
    try {
      loop.awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
// ANCHOR_END: timer-driver
