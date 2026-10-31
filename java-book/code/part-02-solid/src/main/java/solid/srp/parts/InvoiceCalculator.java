package solid.srp.parts;

// ANCHOR: calculator
import java.util.List;
import solid.srp.model.Line;

public final class InvoiceCalculator {
  public long totalCents(List<Line> lines) {
    return lines.stream().mapToLong(Line::cents).sum();
  }
}
// ANCHOR_END: calculator
