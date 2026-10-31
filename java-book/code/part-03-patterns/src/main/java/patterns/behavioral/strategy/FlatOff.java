package patterns.behavioral.strategy;

// ANCHOR: flat-off
public final class FlatOff implements PriceStrategy {
  private final long offCents;

  public FlatOff(long offCents) {
    this.offCents = offCents;
  }

  @Override
  public long price(long baseCents) {
    return Math.max(0, baseCents - offCents);
  }
}
// ANCHOR_END: flat-off
