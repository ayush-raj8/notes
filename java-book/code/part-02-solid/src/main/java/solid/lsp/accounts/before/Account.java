package solid.lsp.accounts.before;

// ANCHOR: account
public class Account {
  protected long balance;

  public Account(long balance) {
    this.balance = balance;
  }

  /**
   * Precondition: 0 &lt; amount &lt;= balance. Postcondition: balance decreases by exactly amount.
   * Invariant: balance &gt;= 0.
   */
  public void withdraw(long amount) {
    if (amount <= 0 || amount > balance) {
      throw new IllegalArgumentException("bad amount");
    }
    balance -= amount;
  }

  public void deposit(long amount) {
    balance += amount;
  }

  public long balance() {
    return balance;
  }
}
// ANCHOR_END: account
