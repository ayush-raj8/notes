package solid.srp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import solid.srp.after.Invoice;
import solid.srp.after.InvoiceService;
import solid.srp.before.InvoiceGod;
import solid.srp.model.Line;
import solid.srp.parts.InMemoryInvoiceRepository;
import solid.srp.parts.InvoiceCalculator;
import solid.srp.parts.InvoiceFormatter;
import solid.srp.parts.OutboxNotifier;
import solid.srp.parts.ReceiptNotifier;

class SrpTest {
  private static final List<Line> LINES = List.of(new Line("A", 2, 4999), new Line("B", 1, 2001));

  @Test
  void godClassCorruptsStoredTotalWhenPrintedFirst() {
    InvoiceGod inv = new InvoiceGod("G");
    LINES.forEach(inv::add);
    Map<String, Long> db = new HashMap<>();
    inv.toText();
    inv.save(db);
    assertEquals(12000L, db.get("G")); // wrong: the real total is 11999
  }

  @Test
  void calculatorNeedsNoIoToTest() {
    assertEquals(11999, new InvoiceCalculator().totalCents(LINES));
    assertEquals(0, new InvoiceCalculator().totalCents(List.of()));
  }

  @Test
  void formatterRoundsHalfUpAndNothingElse() {
    InvoiceFormatter f = new InvoiceFormatter();
    assertEquals("X total=120", f.text("X", 11999));
    assertEquals("X total=3", f.text("X", 250));
  }

  @Test
  void renderingDoesNotChangeWhatIsStored() {
    InMemoryInvoiceRepository repo = new InMemoryInvoiceRepository();
    InvoiceService svc =
        new InvoiceService(
            new InvoiceCalculator(), new InvoiceFormatter(), repo, new OutboxNotifier());
    Invoice inv = new Invoice("S", LINES);
    svc.render(inv);
    svc.process(inv);
    svc.render(inv);
    assertEquals(11999L, repo.find("S").orElseThrow());
  }

  @Test
  void notifierCanBeFakedWithALambda() {
    int[] sent = {0};
    ReceiptNotifier counting = (id, cents) -> sent[0]++;
    InvoiceService svc =
        new InvoiceService(
            new InvoiceCalculator(),
            new InvoiceFormatter(),
            new InMemoryInvoiceRepository(),
            counting);
    svc.process(new Invoice("N", LINES));
    assertEquals(1, sent[0]);
  }

  @Test
  void repositoryIsIndependentOfPricing() {
    InMemoryInvoiceRepository repo = new InMemoryInvoiceRepository();
    repo.save("R", 42);
    assertEquals(42L, repo.find("R").orElseThrow());
    assertTrue(repo.find("missing").isEmpty());
  }
}
