package lld.vending;

import java.util.List;

// ANCHOR: vending-state
/** State pattern: the same three events behave differently per state. */
public sealed interface VendingState permits IdleState, HasMoneyState {
  void insert(VendingMachine machine, Coin coin);

  SelectResult select(VendingMachine machine, String code);

  List<Coin> cancel(VendingMachine machine);
}
// ANCHOR_END: vending-state
