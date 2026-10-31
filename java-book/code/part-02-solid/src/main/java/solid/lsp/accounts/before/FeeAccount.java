package solid.lsp.accounts.before;

// ANCHOR: fee-account
public class FeeAccount extends Account {
  public FeeAccount(long balance) {
    super(balance);
  }

  @Override
  public void withdraw(long amount) {
    super.withdraw(amount);
    balance -= 1; // postcondition now says "decreases by amount + 1"
  }
}
// ANCHOR_END: fee-account
