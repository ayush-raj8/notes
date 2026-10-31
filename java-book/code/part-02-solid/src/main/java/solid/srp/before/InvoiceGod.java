package solid.srp.before;

// ANCHOR: invoice-god
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import solid.srp.model.Line;

public final class InvoiceGod {
  private final String id;
  private final List<Line> lines = new ArrayList<>();
  private long totalCents;

  public InvoiceGod(String id) {
    this.id = id;
  }

  public void add(Line line) {
    lines.add(line);
  }

  // reason 1: pricing rule
  public long total() {
    totalCents = lines.stream().mapToLong(Line::cents).sum();
    return totalCents;
  }

  // reason 2: print layout (whole units; rounds the shared field)
  public String toText() {
    totalCents = Math.round(total() / 100.0) * 100;
    return id + " total=" + totalCents / 100;
  }

  // reason 3: persistence
  public void save(Map<String, Long> db) {
    db.put(id, totalCents);
  }

  // reason 4: notification
  public void sendReceipt(List<String> outbox) {
    outbox.add("receipt " + id + " " + totalCents);
  }
}
// ANCHOR_END: invoice-god
