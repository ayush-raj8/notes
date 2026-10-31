package solid.principles.tda;

// ANCHOR: wallet
/** Owns its rule: callers tell it what to do. */
public final class Wallet {
  private long balance;

  public Wallet(long balance) {
    this.balance = balance;
  }

  public boolean spend(long amount) {
    if (amount <= 0 || amount > balance) {
      return false;
    }
    balance -= amount;
    return true;
  }

  public long balance() {
    return balance;
  }
}
// ANCHOR_END: wallet
