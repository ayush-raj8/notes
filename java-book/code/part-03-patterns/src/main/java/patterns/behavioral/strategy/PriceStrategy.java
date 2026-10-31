package patterns.behavioral.strategy;

// ANCHOR: price-strategy
/** Strategy: one interchangeable algorithm. One abstract method, so lambdas fit. */
@FunctionalInterface
public interface PriceStrategy {
  long price(long baseCents);
}
// ANCHOR_END: price-strategy
