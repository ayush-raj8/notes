package solid.srp.after;

// ANCHOR: after-demo
import java.util.List;
import solid.srp.model.Line;
import solid.srp.parts.InMemoryInvoiceRepository;
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;
import solid.srp.parts.OutboxNotifier;

public final class AfterDemo {
  public static void main(String[] args) {
    InMemoryInvoiceRepository repo = new InMemoryInvoiceRepository();
    OutboxNotifier mail = new OutboxNotifier();
    InvoiceService service =
        new InvoiceService(new InvoiceCalculator(), new InvoiceFormatter(), repo, mail);
    Invoice inv = new Invoice("INV-1", List.of(new Line("A", 2, 4999), new Line("B", 1, 2001)));

    System.out.println("stored before print: " + repo.find("INV-1").orElse(-1L));
    System.out.println("printed: " + service.render(inv));
    service.process(inv);
    System.out.println("printed: " + service.render(inv));
    System.out.println("stored after print:  " + repo.find("INV-1").orElse(-1L));
    System.out.println("outbox: " + mail.outbox);
  }
}
// ANCHOR_END: after-demo
