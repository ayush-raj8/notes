package solid.srp.before;

// ANCHOR: god-failure
import java.util.HashMap;
import java.util.Map;
import solid.srp.model.Line;

public final class GodFailureDemo {
  public static void main(String[] args) {
    InvoiceGod inv = new InvoiceGod("INV-1");
    inv.add(new Line("A", 2, 4999));
    inv.add(new Line("B", 1, 2001));

    Map<String, Long> ledger = new HashMap<>();
    inv.total();
    inv.save(ledger);
    System.out.println("stored before print: " + ledger.get("INV-1"));

    System.out.println("printed: " + inv.toText());
    inv.save(ledger);
    System.out.println("stored after print:  " + ledger.get("INV-1"));
  }
}
// ANCHOR_END: god-failure
