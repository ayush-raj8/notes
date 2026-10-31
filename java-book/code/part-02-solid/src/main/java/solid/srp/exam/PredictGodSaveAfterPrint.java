package solid.srp.exam;

// ANCHOR: predict-god-save
import java.util.HashMap;
import java.util.Map;
import solid.srp.before.InvoiceGod;
import solid.srp.model.Line;

public final class PredictGodSaveAfterPrint {
  public static void main(String[] args) {
    InvoiceGod inv = new InvoiceGod("P1");
    inv.add(new Line("A", 3, 3333));
    Map<String, Long> db = new HashMap<>();
    System.out.println(inv.toText());
    inv.save(db);
    System.out.println(db.get("P1"));
  }
}
// ANCHOR_END: predict-god-save
