package solid.dip.after;

// ANCHOR: composition-root
import java.time.Clock;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;

/** The only place that says `new` for concrete collaborators. */
public final class CompositionRoot {
  public static OrderService production() {
    return new OrderService(new LogMailer(), new MemoryOrderStore(), Clock.systemDefaultZone());
  }
}
// ANCHOR_END: composition-root
