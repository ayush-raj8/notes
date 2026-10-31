package patterns.behavioral.strategy;

// ANCHOR: percent-off
public final class PercentOff implements PriceStrategy {
  private final int percent;

  public PercentOff(int percent) {
    if (percent < 0 || percent > 100) {
      throw new IllegalArgumentException("percent must be 0..100: " + percent);
    }
    this.percent = percent;
  }

  @Override
  public long price(long baseCents) {
    return baseCents * (100 - percent) / 100;
  }
}
// ANCHOR_END: percent-off
