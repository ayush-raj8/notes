package solid.dip.exam;

// ANCHOR: predict-separate-stores
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

public final class PredictSeparateStores {
  public static void main(String[] args) {
    Clock monday = Clock.fixed(Instant.parse("2026-11-02T09:00:00Z"), ZoneOffset.UTC);
    LogMailer m1 = new LogMailer();
    MemoryOrderStore s1 = new MemoryOrderStore();
    LogMailer m2 = new LogMailer();
    MemoryOrderStore s2 = new MemoryOrderStore();
    OrderService one = new OrderService(m1, s1, monday);
    OrderService two = new OrderService(m2, s2, monday);
    one.place(new Order("A", 100));
    one.place(new Order("B", 100));
    two.place(new Order("A", 100));
    System.out.println(m1.sent.size() + " " + m2.sent.size());
    System.out.println(s1.count() + " " + s2.count());
    System.out.println(s2.find("B").isPresent());
  }
}
// ANCHOR_END: predict-separate-stores
