package solid.srp.parts;

// ANCHOR: formatter
public final class InvoiceFormatter {
  public String text(String id, long totalCents) {
    return id + " total=" + Math.round(totalCents / 100.0);
  }
}
// ANCHOR_END: formatter
