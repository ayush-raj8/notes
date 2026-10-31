package solid.lsp.accounts.after;

// ANCHOR: account
/** Step 2: the limit is data, and the precondition is a public query. No subclasses needed. */
public final class Account {
  private long balance;
  private final long limit;

  public Account(long balance) {
    this(balance, Long.MAX_VALUE);
  }

  public Account(long balance, long limit) {
    this.balance = balance;
    this.limit = limit;
  }

  public boolean canWithdraw(long amount) {
    return amount > 0 && amount <= balance && amount <= limit;
  }

  public void withdraw(long amount) {
    if (!canWithdraw(amount)) {
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
