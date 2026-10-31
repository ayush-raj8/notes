package solid.srp.exam;

// ANCHOR: predict-god-half-up
import solid.srp.before.InvoiceGod;
import solid.srp.model.Line;

public final class PredictGodHalfUp {
  public static void main(String[] args) {
    InvoiceGod inv = new InvoiceGod("P2");
    inv.add(new Line("A", 1, 250));
    System.out.println(inv.toText());
    System.out.println(inv.toText());
    System.out.println(inv.total());
  }
}
// ANCHOR_END: predict-god-half-up
