package solid.dip.solutions;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

/** Exercise 3: one composition root that wires differently for "prod" and "test". */
public final class SolutionRootByMode {
  static OrderService wire(String mode) {
    if (mode.equals("test")) {
      Clock monday = Clock.fixed(Instant.parse("2026-11-02T09:00:00Z"), ZoneOffset.UTC);
      Mailer silent = (to, body) -> {};
      return new OrderService(silent, new MemoryOrderStore(), monday);
    }
    return new OrderService(new LogMailer(), new MemoryOrderStore(), Clock.systemUTC());
  }

  public static void main(String[] args) {
    System.out.println(wire("test").place(new Order("A", 200)));
  }
}
