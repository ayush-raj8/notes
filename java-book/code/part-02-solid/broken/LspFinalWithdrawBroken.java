package solid.lsp;

import solid.lsp.accounts.step1.AccountStep1;

/** withdraw() is final in step 1: the contract-enforcing template cannot be replaced. */
public final class LspFinalWithdrawBroken extends AccountStep1 {
  public LspFinalWithdrawBroken() {
    super(0);
  }

  @Override
  public void withdraw(long amount) {
    balance -= amount + 1;
  }
}
