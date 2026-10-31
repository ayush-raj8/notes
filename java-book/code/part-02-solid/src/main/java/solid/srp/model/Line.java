package solid.srp.model;

// ANCHOR: line
public record Line(String sku, int qty, long unitCents) {
  public long cents() {
    return qty * unitCents;
  }
}
// ANCHOR_END: line
