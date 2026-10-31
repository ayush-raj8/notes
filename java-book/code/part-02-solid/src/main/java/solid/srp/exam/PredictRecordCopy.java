package solid.srp.exam;

// ANCHOR: predict-record-copy
import java.util.ArrayList;
import java.util.List;
import solid.srp.after.Invoice;
import solid.srp.model.Line;
import solid.srp.parts.InvoiceCalculator;

public final class PredictRecordCopy {
  public static void main(String[] args) {
    List<Line> lines = new ArrayList<>();
    lines.add(new Line("A", 1, 100));
    Invoice inv = new Invoice("P5", lines);
    lines.add(new Line("B", 1, 900));
    System.out.println(inv.lines().size());
    System.out.println(new InvoiceCalculator().totalCents(inv.lines()));
    try {
      inv.lines().add(new Line("C", 1, 1));
    } catch (UnsupportedOperationException e) {
      System.out.println("immutable");
    }
  }
}
// ANCHOR_END: predict-record-copy
