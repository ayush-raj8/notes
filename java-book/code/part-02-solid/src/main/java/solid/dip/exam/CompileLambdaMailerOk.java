package solid.dip.exam;

// ANCHOR: compile-ok-lambda
import java.time.Clock;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.model.Order;

public final class CompileLambdaMailerOk {
  public static void main(String[] args) {
    Mailer console = (to, body) -> System.out.println("to=" + to + " body=" + body);
    OrderService svc = new OrderService(console, new MemoryOrderStore(), Clock.systemUTC());
    System.out.println(svc.place(new Order("Z", 0)));
  }
}
// ANCHOR_END: compile-ok-lambda
