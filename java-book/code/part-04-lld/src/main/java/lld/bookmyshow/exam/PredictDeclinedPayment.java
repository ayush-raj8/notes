package lld.bookmyshow.exam;

import java.time.Duration;
import java.util.List;
import lld.bookmyshow.*;

public class PredictDeclinedPayment {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(SampleData.T0);
    Catalog catalog = new Catalog();
    catalog.add(SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1))));
    BookingService svc =
        new BookingService(catalog, (user, paise) -> paise <= 30_000, clock, Duration.ofMinutes(5));
    SeatHold small = svc.hold("s", "asha", List.of("A1"));
    System.out.println(svc.checkout(small.holdId()).status());
    SeatHold big = svc.hold("s", "ben", List.of("A2", "A3"));
    try {
      svc.checkout(big.holdId());
    } catch (PaymentFailedException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(catalog.show("s").statusAt("A2", clock.instant()));
    try {
      svc.checkout(big.holdId());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
