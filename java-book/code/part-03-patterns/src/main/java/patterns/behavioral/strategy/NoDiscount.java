package patterns.behavioral.strategy;

// ANCHOR: no-discount
public final class NoDiscount implements PriceStrategy {
  @Override
  public long price(long baseCents) {
    return baseCents;
  }
}
// ANCHOR_END: no-discount
