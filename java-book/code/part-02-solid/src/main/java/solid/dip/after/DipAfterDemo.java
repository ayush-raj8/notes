package solid.dip.after;

// ANCHOR: dip-after
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

public final class DipAfterDemo {
  static void run(String label, String instant) {
    LogMailer mail = new LogMailer();
    MemoryOrderStore store = new MemoryOrderStore();
    Clock fixed = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
    long total = new OrderService(mail, store, fixed).place(new Order("A", 10_000));
    System.out.println(label + " total=" + total + " mails=" + mail.sent.size() + " stored=" + store.count());
  }

  public static void main(String[] args) {
    run("saturday", "2026-10-31T10:00:00Z");
    run("monday  ", "2026-11-02T10:00:00Z");
  }
}
// ANCHOR_END: dip-after
