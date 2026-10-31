package solid.srp.step4;

// ANCHOR: step4
import java.util.ArrayList;
import java.util.List;
import solid.srp.model.Line;
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;
import solid.srp.parts.InvoiceRepository;
import solid.srp.parts.ReceiptNotifier;

/** Step 4: notification moved behind an interface. What is left is data plus orchestration. */
public final class InvoiceStep4 {
  private final String id;
  private final List<Line> lines = new ArrayList<>();
  private final InvoiceCalculator calculator = new InvoiceCalculator();
  private final InvoiceFormatter formatter = new InvoiceFormatter();
  private final InvoiceRepository repository;
  private final ReceiptNotifier notifier;

  public InvoiceStep4(String id, InvoiceRepository repository, ReceiptNotifier notifier) {
    this.id = id;
    this.repository = repository;
    this.notifier = notifier;
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

  public void process() {
    repository.save(id, total());
    notifier.send(id, total());
  }
}
// ANCHOR_END: step4
