package solid.dip;

import java.time.Clock;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;

/** Constructor injection is checked by types: swapped collaborators do not compile. */
public final class DipArgOrderBroken {
  public static void main(String[] args) {
    OrderService svc = new OrderService(new MemoryOrderStore(), new LogMailer(), Clock.systemUTC());
  }
}
