package solid.dip.step2;

// ANCHOR: step2
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderStore;
import solid.dip.model.Order;

/** Step 2: the collaborators arrive through the constructor. */
public final class OrderServiceStep2 {
  private final Mailer mailer;
  private final OrderStore store;
  private final Clock clock;

  public OrderServiceStep2(Mailer mailer, OrderStore store, Clock clock) {
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
// ANCHOR_END: step2
