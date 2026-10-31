package lld.vending;

import java.util.List;

// ANCHOR: select-result
/** Closed set of outcomes: callers handle both with an exhaustive switch (Java 21). */
public sealed interface SelectResult permits SelectResult.Dispensed, SelectResult.Rejected {
  record Dispensed(Product product, List<Coin> change) implements SelectResult {}

  record Rejected(String reason) implements SelectResult {}
}
// ANCHOR_END: select-result
