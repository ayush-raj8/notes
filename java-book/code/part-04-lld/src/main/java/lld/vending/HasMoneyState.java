package lld.vending;

import java.util.List;
import java.util.Optional;

// ANCHOR: has-money-state
public final class HasMoneyState implements VendingState {
  public static final HasMoneyState INSTANCE = new HasMoneyState();

  private HasMoneyState() {}

  @Override
  public void insert(VendingMachine m, Coin coin) {
    m.deposit(coin);
  }

  @Override
  public SelectResult select(VendingMachine m, String code) {
    Optional<Product> found = m.inventory().find(code);
    if (found.isEmpty()) {
      return new SelectResult.Rejected("UNKNOWN_PRODUCT");
    }
    Product product = found.get();
    if (m.inventory().quantity(code) == 0) {
      return new SelectResult.Rejected("SOLD_OUT");
    }
    if (m.balanceCents() < product.price()) {
      return new SelectResult.Rejected(
          "INSUFFICIENT: need " + (product.price() - m.balanceCents()) + " more");
    }
    Optional<List<Coin>> change = m.bank().withdraw(m.balanceCents() - product.price());
    if (change.isEmpty()) {
      return new SelectResult.Rejected("NO_CHANGE");
    }
    m.inventory().take(code);
    m.clearBalance();
    m.setState(IdleState.INSTANCE);
    return new SelectResult.Dispensed(product, change.get());
  }

  @Override
  public List<Coin> cancel(VendingMachine m) {
    List<Coin> refund = m.bank().withdraw(m.balanceCents())
        .orElseThrow(() -> new IllegalStateException("inserted coins must still be in the bank"));
    m.clearBalance();
    m.setState(IdleState.INSTANCE);
    return refund;
  }
}
// ANCHOR_END: has-money-state
