package lld.bookmyshow.exam;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lld.bookmyshow.*;

public class PredictExpiryBoundary {
  public static void main(String[] args) {
    Show show = SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1)));
    SeatHold h = show.hold("H1", "asha", List.of("B1"), SampleData.T0, Duration.ofMinutes(5));
    Instant justBefore = h.expiresAt().minusSeconds(1);
    System.out.println(show.statusAt("B1", justBefore) instanceof SeatStatus.Held);
    System.out.println(show.statusAt("B1", h.expiresAt()) instanceof SeatStatus.Available);
    System.out.println(h.expiresAt());
  }
}
