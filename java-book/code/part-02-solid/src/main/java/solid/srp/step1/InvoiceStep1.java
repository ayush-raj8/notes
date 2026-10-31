package solid.srp.step1;

// ANCHOR: step1
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import solid.srp.model.Line;
import solid.srp.parts.InvoiceCalculator;

/** Step 1: pricing moved out; the shared mutable total field is gone. */
public final class InvoiceStep1 {
  private final String id;
  private final List<Line> lines = new ArrayList<>();
  private final InvoiceCalculator calculator = new InvoiceCalculator();

  public InvoiceStep1(String id) {
    this.id = id;
  }

  public void add(Line line) {
    lines.add(line);
  }

  public long total() {
    return calculator.totalCents(lines);
  }

  public String toText() {
    return id + " total=" + Math.round(total() / 100.0);
  }

  public void save(Map<String, Long> db) {
    db.put(id, total());
  }

  public void sendReceipt(List<String> outbox) {
    outbox.add("receipt " + id + " " + total());
  }
}
// ANCHOR_END: step1
