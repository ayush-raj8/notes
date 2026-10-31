package solid.dip.after.domain;

// ANCHOR: order-service
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import solid.dip.model.Order;

public final class OrderService {
  private final Mailer mailer;
  private final OrderStore store;
  private final Clock clock;

  public OrderService(Mailer mailer, OrderStore store, Clock clock) {
    this.mailer = mailer;
    this.store = store;
    this.clock = clock;
  }

  public long place(Order o) {
    DayOfWeek today = LocalDate.now(clock).getDayOfWeek();
    boolean weekend = today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    long total = weekend ? o.cents() * 105 / 100 : o.cents();
    store.save(o.id(), total);
    mailer.send("ops@example.com", "order " + o.id() + " " + total);
    return total;
  }
}
// ANCHOR_END: order-service
