package solid.srp.step3;

// ANCHOR: step3
import java.util.ArrayList;
import java.util.List;
import solid.srp.model.Line;
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;
import solid.srp.parts.InvoiceRepository;

/** Step 3: persistence moved behind an interface. */
public final class InvoiceStep3 {
  private final String id;
  private final List<Line> lines = new ArrayList<>();
  private final InvoiceCalculator calculator = new InvoiceCalculator();
  private final InvoiceFormatter formatter = new InvoiceFormatter();
  private final InvoiceRepository repository;

  public InvoiceStep3(String id, InvoiceRepository repository) {
    this.id = id;
    this.repository = repository;
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

  public void save() {
    repository.save(id, total());
  }

  public void sendReceipt(List<String> outbox) {
    outbox.add("receipt " + id + " " + total());
  }
}
// ANCHOR_END: step3
