package lld.bookmyshow.exam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lld.bookmyshow.*;

public class PredictCancelTwice {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(SampleData.T0);
    Catalog catalog = new Catalog();
    catalog.add(SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1))));
    List<String> refunds = new ArrayList<>();
    PaymentGateway gw =
        new PaymentGateway() {
          public boolean charge(String user, long paise) {
            return true;
          }

          @Override
          public void refund(String user, long paise) {
            refunds.add(user + ":" + paise);
          }
        };
    BookingService svc = new BookingService(catalog, gw, clock, Duration.ofMinutes(5));
    Booking b = svc.checkout(svc.hold("s", "asha", List.of("B1", "B2")).holdId());
    System.out.println(svc.cancel(b.bookingId()).status());
    try {
      svc.cancel(b.bookingId());
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(refunds);
    clock.advance(Duration.ofHours(2));
    System.out.println(svc.bookingsOf("asha").get(0).status());
  }
}
