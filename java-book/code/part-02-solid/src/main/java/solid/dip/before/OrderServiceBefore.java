package solid.dip.before;

// ANCHOR: order-service-before
import java.time.DayOfWeek;
import java.time.LocalDate;
import solid.dip.model.Order;

public final class OrderServiceBefore {
  public long place(Order o) {
    DayOfWeek today = LocalDate.now().getDayOfWeek(); // hidden dependency: the real clock
    boolean weekend = today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    long total = weekend ? o.cents() * 105 / 100 : o.cents();
    new SqlOrderStore().save(o.id(), total); // hidden dependency: a concrete store
    new SmtpMailer().send("ops@example.com", "order " + o.id() + " " + total);
    return total;
  }
}
// ANCHOR_END: order-service-before
