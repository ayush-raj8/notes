package solid.dip.exam;

// ANCHOR: predict-failing-mailer
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

public final class PredictFailingMailer {
  public static void main(String[] args) {
    Mailer down =
        (to, body) -> {
          throw new IllegalStateException("smtp down");
        };
    MemoryOrderStore store = new MemoryOrderStore();
    Clock monday = Clock.fixed(Instant.parse("2026-11-02T09:00:00Z"), ZoneOffset.UTC);
    OrderService svc = new OrderService(down, store, monday);
    try {
      svc.place(new Order("A", 500));
      System.out.println("placed");
    } catch (IllegalStateException e) {
      System.out.println("failed: " + e.getMessage());
    }
    System.out.println("stored=" + store.count());
  }
}
// ANCHOR_END: predict-failing-mailer
