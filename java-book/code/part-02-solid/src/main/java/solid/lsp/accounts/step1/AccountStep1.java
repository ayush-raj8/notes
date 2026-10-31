package solid.lsp.accounts.step1;

// ANCHOR: step1
/** Step 1: the base class enforces its own postcondition and forbids overriding withdraw(). */
public class AccountStep1 {
  protected long balance;

  public AccountStep1(long balance) {
    this.balance = balance;
  }

  public final void withdraw(long amount) {
    if (amount <= 0 || amount > balance) {
      throw new IllegalArgumentException("bad amount");
    }
    long expected = balance - amount;
    doWithdraw(amount);
    if (balance != expected) {
      throw new IllegalStateException(
          "postcondition violated: expected " + expected + " but was " + balance);
    }
  }

  protected void doWithdraw(long amount) {
    balance -= amount;
  }

  public long balance() {
    return balance;
  }

  /** A subclass that still tries the old trick is caught at runtime, not in production data. */
  public static final class Fee extends AccountStep1 {
    public Fee(long balance) {
      super(balance);
    }

    @Override
    protected void doWithdraw(long amount) {
      balance -= amount + 1;
    }
  }
}
// ANCHOR_END: step1
