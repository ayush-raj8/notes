package solid.principles.tda;

// ANCHOR: loose-wallet
/** Data holder: every caller must remember the rules. */
public final class LooseWallet {
  private long balance;

  public LooseWallet(long balance) {
    this.balance = balance;
  }

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }
}
// ANCHOR_END: loose-wallet
