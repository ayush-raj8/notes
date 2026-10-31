package solid.lsp.accounts.before;

// ANCHOR: capped-account
public class CappedAccount extends Account {
  public CappedAccount(long balance) {
    super(balance);
  }

  @Override
  public void withdraw(long amount) {
    if (amount > 500) { // strengthens the precondition
      throw new IllegalArgumentException("limit 500");
    }
    super.withdraw(amount);
  }
}
// ANCHOR_END: capped-account
