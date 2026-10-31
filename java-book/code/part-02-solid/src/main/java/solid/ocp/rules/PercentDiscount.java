package solid.ocp.rules;

// ANCHOR: percent-discount
public record PercentDiscount(int percent) implements Discount {
  @Override
  public long amount(long cents) {
    return cents * percent / 100;
  }
}
// ANCHOR_END: percent-discount
