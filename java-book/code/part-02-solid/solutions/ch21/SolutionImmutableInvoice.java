package solid.srp.solutions;

import java.util.ArrayList;
import java.util.List;
import solid.srp.after.Invoice;
import solid.srp.model.Line;
import solid.srp.parts.InvoiceCalculator;

/** Exercise 5: add lines to a data-only Invoice by returning a new one. */
public final class SolutionImmutableInvoice {
  static Invoice withLine(Invoice inv, Line line) {
    List<Line> next = new ArrayList<>(inv.lines());
    next.add(line);
    return new Invoice(inv.id(), next);
  }

  public static void main(String[] args) {
    Invoice a = new Invoice("I", List.of(new Line("A", 1, 100)));
    Invoice b = withLine(a, new Line("B", 2, 100));
    InvoiceCalculator calc = new InvoiceCalculator();
    System.out.println(calc.totalCents(a.lines()) + " " + calc.totalCents(b.lines()));
  }
}
