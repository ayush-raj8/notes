package solid.dip.exam;

// ANCHOR: predict-fixed-clock
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

public final class PredictFixedClock {
  static long place(String instant, long cents) {
    Clock c = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
    return new OrderService(new LogMailer(), new MemoryOrderStore(), c).place(new Order("X", cents));
  }

  public static void main(String[] args) {
    System.out.println(place("2026-10-31T10:00:00Z", 10_000)); // Saturday
    System.out.println(place("2026-11-01T10:00:00Z", 999)); // Sunday
    System.out.println(place("2026-11-02T10:00:00Z", 10_000)); // Monday
  }
}
// ANCHOR_END: predict-fixed-clock
