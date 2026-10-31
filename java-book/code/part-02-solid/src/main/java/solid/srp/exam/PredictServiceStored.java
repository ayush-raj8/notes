package solid.srp.exam;

// ANCHOR: predict-service-stored
import java.util.List;
import solid.srp.after.Invoice;
import solid.srp.after.InvoiceService;
import solid.srp.model.Line;
import solid.srp.parts.InMemoryInvoiceRepository;
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;
import solid.srp.parts.OutboxNotifier;

public final class PredictServiceStored {
  public static void main(String[] args) {
    InMemoryInvoiceRepository repo = new InMemoryInvoiceRepository();
    InvoiceService svc =
        new InvoiceService(
            new InvoiceCalculator(), new InvoiceFormatter(), repo, new OutboxNotifier());
    Invoice inv = new Invoice("P3", List.of(new Line("A", 3, 3333)));
    System.out.println(svc.render(inv));
    svc.process(inv);
    System.out.println(repo.find("P3").orElse(-1L));
    System.out.println(repo.find("none").isPresent());
  }
}
// ANCHOR_END: predict-service-stored
