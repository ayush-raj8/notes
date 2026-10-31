package lld.vending;

import java.util.List;

// ANCHOR: idle-state
public final class IdleState implements VendingState {
  public static final IdleState INSTANCE = new IdleState();

  private IdleState() {}

  @Override
  public void insert(VendingMachine m, Coin coin) {
    m.deposit(coin);
    m.setState(HasMoneyState.INSTANCE);
  }

  @Override
  public SelectResult select(VendingMachine m, String code) {
    return new SelectResult.Rejected("INSERT_COIN");
  }

  @Override
  public List<Coin> cancel(VendingMachine m) {
    return List.of();
  }
}
// ANCHOR_END: idle-state
