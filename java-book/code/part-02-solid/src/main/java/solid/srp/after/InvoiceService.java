package solid.srp.after;

// ANCHOR: service
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;
import solid.srp.parts.InvoiceRepository;
import solid.srp.parts.ReceiptNotifier;

public final class InvoiceService {
  private final InvoiceCalculator calculator;
  private final InvoiceFormatter formatter;
  private final InvoiceRepository repository;
  private final ReceiptNotifier notifier;

  public InvoiceService(
      InvoiceCalculator calculator,
      InvoiceFormatter formatter,
      InvoiceRepository repository,
      ReceiptNotifier notifier) {
    this.calculator = calculator;
    this.formatter = formatter;
    this.repository = repository;
    this.notifier = notifier;
  }

  public String render(Invoice invoice) {
    return formatter.text(invoice.id(), calculator.totalCents(invoice.lines()));
  }

  public long process(Invoice invoice) {
    long total = calculator.totalCents(invoice.lines());
    repository.save(invoice.id(), total);
    notifier.send(invoice.id(), total);
    return total;
  }
}
// ANCHOR_END: service
