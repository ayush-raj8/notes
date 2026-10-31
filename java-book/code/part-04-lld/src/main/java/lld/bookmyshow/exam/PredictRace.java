package lld.bookmyshow.exam;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import lld.bookmyshow.*;

public class PredictRace {
  public static void main(String[] args) throws Exception {
    Show show = SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1)));
    int users = 20;
    CountDownLatch go = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(users);
    AtomicInteger wins = new AtomicInteger();
    AtomicInteger losses = new AtomicInteger();
    for (int i = 0; i < users; i++) {
      String holdId = "H" + i;
      new Thread(
              () -> {
                try {
                  go.await();
                  show.hold(holdId, "u" + holdId, List.of("A1"), SampleData.T0, Duration.ofMinutes(5));
                  wins.incrementAndGet();
                } catch (SeatUnavailableException e) {
                  losses.incrementAndGet();
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                } finally {
                  done.countDown();
                }
              })
          .start();
    }
    go.countDown();
    done.await();
    System.out.println("wins=" + wins + " losses=" + losses);
  }
}
