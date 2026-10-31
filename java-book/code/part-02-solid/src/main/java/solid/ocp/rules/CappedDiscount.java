package solid.ocp.rules;

// ANCHOR: capped-discount
/** Added after the registry existed: no existing file was edited. */
public record CappedDiscount(int percent, long capCents) implements Discount {
  @Override
  public long amount(long cents) {
    return Math.min(cents * percent / 100, capCents);
  }
}
// ANCHOR_END: capped-discount
