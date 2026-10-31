package lld.vending;

import java.util.List;

// ANCHOR: vending-machine
/** Context of the State pattern. Public methods are the only entry points and are synchronized. */
public final class VendingMachine {
  private final Inventory inventory;
  private final CoinBank bank;
  private int balance;
  private VendingState state = IdleState.INSTANCE;

  public VendingMachine(Inventory inventory, CoinBank bank) {
    this.inventory = inventory;
    this.bank = bank;
  }

  public synchronized void insert(Coin coin) {
    state.insert(this, coin);
  }

  public synchronized SelectResult select(String code) {
    return state.select(this, code);
  }

  public synchronized List<Coin> cancel() {
    return state.cancel(this);
  }

  public synchronized int balance() {
    return balance;
  }

  public synchronized String stateName() {
    return state.getClass().getSimpleName();
  }

  // Package-private: only the state classes may touch these.
  void deposit(Coin coin) {
    bank.deposit(coin);
    balance += coin.cents();
  }

  Inventory inventory() { return inventory; }
  CoinBank bank() { return bank; }
  int balanceCents() { return balance; }
  void clearBalance() { balance = 0; }
  void setState(VendingState next) { state = next; }
}
// ANCHOR_END: vending-machine
