package patterns.behavioral.strategy;

/** Intentionally invalid: interface methods are public; the implementation cannot weaken that. */
public final class StrategyNarrowAccessBroken implements PriceStrategy {
  long price(long baseCents) {
    return baseCents;
  }
}
