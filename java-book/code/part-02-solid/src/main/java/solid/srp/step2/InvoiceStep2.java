package solid.srp.step2;

// ANCHOR: step2
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import solid.srp.model.Line;
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;

/** Step 2: print layout moved out. */
public final class InvoiceStep2 {
  private final String id;
  private final List<Line> lines = new ArrayList<>();
  private final InvoiceCalculator calculator = new InvoiceCalculator();
  private final InvoiceFormatter formatter = new InvoiceFormatter();

  public InvoiceStep2(String id) {
    this.id = id;
  }

  public void add(Line line) {
    lines.add(line);
  }

  public long total() {
    return calculator.totalCents(lines);
  }

  public String toText() {
    return formatter.text(id, total());
  }

  public void save(Map<String, Long> db) {
    db.put(id, total());
  }

  public void sendReceipt(List<String> outbox) {
    outbox.add("receipt " + id + " " + total());
  }
}
// ANCHOR_END: step2
