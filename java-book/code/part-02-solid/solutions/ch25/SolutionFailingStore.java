package solid.dip.solutions;

import java.time.Clock;
import java.util.Optional;
import solid.dip.after.domain.OrderService;
import solid.dip.after.domain.OrderStore;
import solid.dip.after.infra.LogMailer;
import solid.dip.model.Order;

/** Exercise 2: a store that always fails, to test the error path. */
public final class SolutionFailingStore implements OrderStore {
  @Override
  public void save(String id, long total) {
    throw new IllegalStateException("disk full");
  }

  @Override
  public Optional<Long> find(String id) {
    return Optional.empty();
  }

  @Override
  public int count() {
    return 0;
  }

  public static void main(String[] args) {
    LogMailer mail = new LogMailer();
    OrderService svc = new OrderService(mail, new SolutionFailingStore(), Clock.systemUTC());
    try {
      svc.place(new Order("A", 100));
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage() + ", mails=" + mail.sent.size());
    }
  }
}
