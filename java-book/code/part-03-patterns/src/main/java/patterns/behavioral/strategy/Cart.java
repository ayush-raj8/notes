package patterns.behavioral.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// ANCHOR: cart
/** Context: owns the data, delegates the pricing algorithm to the current strategy. */
public final class Cart {
  private final List<Long> items = new ArrayList<>();
  private PriceStrategy strategy;

  public Cart(PriceStrategy strategy) {
    this.strategy = Objects.requireNonNull(strategy, "strategy");
  }

  public Cart add(long cents) {
    items.add(cents);
    return this;
  }

  public void setStrategy(PriceStrategy strategy) {
    this.strategy = Objects.requireNonNull(strategy, "strategy");
  }

  public long total() {
    long base = items.stream().mapToLong(Long::longValue).sum();
    return strategy.price(base);
  }
}
// ANCHOR_END: cart
