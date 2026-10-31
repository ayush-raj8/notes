package lld.bookmyshow;

import java.time.Duration;
import java.util.List;

public final class BookMyShowDemo {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(SampleData.T0);
    Catalog catalog = new Catalog();
    catalog.add(SampleData.show("show-1", SampleData.T0.plus(Duration.ofHours(3))));
    catalog.add(SampleData.show("show-2", SampleData.T0.plus(Duration.ofHours(6))));
    BookingService service =
        new BookingService(catalog, (user, paise) -> true, clock, Duration.ofMinutes(5));

    List<Show> found = catalog.search("pune", "dune", clock.instant());
    System.out.println("search -> " + found.stream().map(Show::id).toList());

    SeatHold asha = service.hold("show-1", "asha", List.of("A1", "A2"));
    System.out.println("asha holds " + asha.seatIds() + " total=" + asha.totalPaise());

    try {
      service.hold("show-1", "ben", List.of("A2", "A3"));
    } catch (SeatUnavailableException e) {
      System.out.println("ben -> " + e.getMessage());
    }
    System.out.println("A3 after ben's failed try: " + catalog.show("show-1").statusAt("A3", clock.instant()));

    Booking b = service.checkout(asha.holdId());
    System.out.println("asha booked " + b.bookingId() + " " + b.status());

    SeatHold ben = service.hold("show-1", "ben", List.of("B1"));
    clock.advance(Duration.ofMinutes(5));
    try {
      service.checkout(ben.holdId());
    } catch (HoldExpiredException e) {
      System.out.println("ben -> " + e.getMessage());
    }
    System.out.println("B1 after expiry: " + catalog.show("show-1").statusAt("B1", clock.instant()));

    service.cancel(b.bookingId());
    System.out.println("after cancel: " + catalog.show("show-1").statusAt("A1", clock.instant()));
  }
}
