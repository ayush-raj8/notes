import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lld.bookmyshow.*;

/** Exercise solutions for chapter 59. */
public class Solutions59 {
  /** Ex 1: free seats per category. */
  static Map<SeatCategory, Integer> freeByCategory(Show show, Instant now) {
    Map<SeatCategory, Integer> out = new EnumMap<>(SeatCategory.class);
    for (Seat s : show.availableSeats(now)) {
      out.merge(s.category(), 1, Integer::sum);
    }
    return out;
  }

  /** Ex 2: first run of {@code n} free seats in one row (row = first character of the id). */
  static List<String> bestAdjacent(Show show, Instant now, int n) {
    List<Seat> free = show.availableSeats(now);
    List<String> run = new ArrayList<>();
    for (Seat s : free) {
      boolean sameRow = !run.isEmpty() && run.get(run.size() - 1).charAt(0) == s.id().charAt(0);
      boolean next =
          sameRow
              && Integer.parseInt(s.id().substring(1))
                  == Integer.parseInt(run.get(run.size() - 1).substring(1)) + 1;
      if (!next) {
        run.clear();
      }
      run.add(s.id());
      if (run.size() == n) {
        return run;
      }
    }
    return List.of();
  }

  /** Ex 3: a gateway that declines above a limit and records refunds. */
  static final class LimitGateway implements PaymentGateway {
    final long limit;
    final List<Long> refunded = new ArrayList<>();

    LimitGateway(long limit) {
      this.limit = limit;
    }

    @Override
    public boolean charge(String userId, long paise) {
      return paise <= limit;
    }

    @Override
    public void refund(String userId, long paise) {
      refunded.add(paise);
    }
  }

  /** Ex 4: revenue of confirmed bookings per user. */
  static Map<String, Long> revenueByUser(BookingService svc) {
    return svc.allBookings().stream()
        .filter(b -> b.status() == BookingStatus.CONFIRMED)
        .collect(Collectors.groupingBy(Booking::userId, Collectors.summingLong(Booking::amountPaise)));
  }

  /** Ex 5: a hold survives until the instant before expiry and is gone at expiry. */
  static String expiryTrace() {
    ManualClock clock = new ManualClock(SampleData.T0);
    Show show = SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1)));
    show.hold("H1", "u", List.of("A1"), clock.instant(), Duration.ofMinutes(5));
    StringBuilder sb = new StringBuilder();
    for (int minute : new int[] {0, 4, 5}) {
      clock = new ManualClock(SampleData.T0.plus(Duration.ofMinutes(minute)));
      sb.append(minute)
          .append(':')
          .append(show.statusAt("A1", clock.instant()).getClass().getSimpleName())
          .append(' ');
    }
    return sb.toString().trim();
  }

  public static void main(String[] args) {
    ManualClock clock = new ManualClock(SampleData.T0);
    Catalog catalog = new Catalog();
    catalog.add(SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1))));
    LimitGateway gw = new LimitGateway(50_000);
    BookingService svc = new BookingService(catalog, gw, clock, Duration.ofMinutes(5));
    Show show = catalog.show("s");

    System.out.println("ex1 " + freeByCategory(show, clock.instant()));
    svc.hold("s", "u0", List.of("A2"));
    System.out.println("ex2 " + bestAdjacent(show, clock.instant(), 3));
    svc.checkout(svc.hold("s", "asha", List.of("B1")).holdId());
    svc.checkout(svc.hold("s", "asha", List.of("B2")).holdId());
    try {
      svc.checkout(svc.hold("s", "ben", List.of("A3", "A4")).holdId());
    } catch (PaymentFailedException e) {
      System.out.println("ex3 " + e.getMessage());
    }
    Booking b = svc.checkout(svc.hold("s", "ben", List.of("B3")).holdId());
    svc.cancel(b.bookingId());
    System.out.println("ex3 refunds " + gw.refunded);
    System.out.println("ex4 " + new java.util.TreeMap<>(revenueByUser(svc)));
    System.out.println("ex5 " + expiryTrace());
  }
}
