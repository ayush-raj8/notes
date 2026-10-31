package solid.dip.exam;

// ANCHOR: predict-call-order
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderService;
import solid.dip.after.domain.OrderStore;
import solid.dip.model.Order;

public final class PredictCallOrder {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    Mailer mailer = (to, body) -> log.add("mail " + body);
    OrderStore store =
        new OrderStore() {
          public void save(String id, long total) {
            log.add("save " + id);
          }

          public Optional<Long> find(String id) {
            return Optional.empty();
          }

          public int count() {
            return 0;
          }
        };
    Clock monday = Clock.fixed(Instant.parse("2026-11-02T09:00:00Z"), ZoneOffset.UTC);
    new OrderService(mailer, store, monday).place(new Order("A", 500));
    System.out.println(log);
  }
}
// ANCHOR_END: predict-call-order
