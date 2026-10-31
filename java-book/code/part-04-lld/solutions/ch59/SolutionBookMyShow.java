import java.time.Duration;
import java.util.List;
import lld.bookmyshow.*;

/** Exercises 1-5 of chapter 59. */
public class SolutionBookMyShow {
  // Ex 1: total for a hold without going through BookingService.
  static long holdTotal(Show show, List<String> seatIds) {
    return seatIds.stream().mapToLong(show::priceOf).sum();
  }

  // Ex 2: seats still free after a failed multi-seat hold.
  static int freeAfterConflict(Show show) {
    show.hold("H1", "asha", List.of("A2"), SampleData.T0, Duration.ofMinutes(5));
    try {
      show.hold("H2", "ben", List.of("A1", "A2", "A3"), SampleData.T0, Duration.ofMinutes(5));
    } catch (SeatUnavailableException ignored) {
    }
    return show.availableSeats(SampleData.T0).size();
  }

  // Ex 3: checkout after TTL using ManualClock.
  static String checkoutAfterTtl() {
    ManualClock clock = new ManualClock(SampleData.T0);
    Catalog catalog = new Catalog();
    catalog.add(SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1))));
    BookingService svc = new BookingService(catalog, (u, p) -> true, clock, Duration.ofMinutes(5));
    SeatHold hold = svc.hold("s", "ben", List.of("B1"));
    clock.advance(Duration.ofMinutes(5));
    try {
      svc.checkout(hold.holdId());
      return "ok";
    } catch (HoldExpiredException e) {
      return e.getMessage();
    }
  }

  // Ex 4: sealed switch label for every seat status.
  static String label(SeatStatus s) {
    return switch (s) {
      case SeatStatus.Available a -> "free";
      case SeatStatus.Held h -> "held:" + h.holdId();
      case SeatStatus.Booked b -> "booked:" + b.bookingId();
    };
  }

  public static void main(String[] args) {
    Show show = SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1)));
    System.out.println(holdTotal(show, List.of("A1", "B1")));
    System.out.println(freeAfterConflict(SampleData.show("s2", SampleData.T0.plus(Duration.ofHours(1)))));
    System.out.println(checkoutAfterTtl());

    Show labeled = SampleData.show("s3", SampleData.T0.plus(Duration.ofHours(1)));
    SeatHold h = labeled.hold("H9", "asha", List.of("A1"), SampleData.T0, Duration.ofMinutes(5));
    labeled.confirm(h, "B9", SampleData.T0);
    System.out.println(label(labeled.statusAt("A1", SampleData.T0)));
    System.out.println(label(labeled.statusAt("A2", SampleData.T0)));

    // Ex 5: money rule — paise as long, never double.
    long paise = 30_000L;
    System.out.println(paise * 118 / 100);
  }
}
