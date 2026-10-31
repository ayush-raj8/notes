package solid.dip.step1;

// ANCHOR: step1
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderStore;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

/** Step 1: fields use abstractions, but the constructor still picks the concrete classes. */
public final class OrderServiceStep1 {
  private final Mailer mailer = new LogMailer();
  private final OrderStore store = new MemoryOrderStore();
  private final Clock clock = Clock.systemDefaultZone();

  public long place(Order o) {
    DayOfWeek today = LocalDate.now(clock).getDayOfWeek();
    boolean weekend = today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    long total = weekend ? o.cents() * 105 / 100 : o.cents();
    store.save(o.id(), total);
    mailer.send("ops@example.com", "order " + o.id() + " " + total);
    return total;
  }

  public int stored() {
    return store.count();
  }
}
// ANCHOR_END: step1
