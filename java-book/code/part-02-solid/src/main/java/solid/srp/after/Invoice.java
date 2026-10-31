package solid.srp.after;

// ANCHOR: invoice
import java.util.List;
import solid.srp.model.Line;

public record Invoice(String id, List<Line> lines) {
  public Invoice {
    lines = List.copyOf(lines);
  }
}
// ANCHOR_END: invoice
