package solid.lsp.exam;

// ANCHOR: predict-fee-transfer
import solid.lsp.accounts.before.Account;
import solid.lsp.accounts.before.FeeAccount;
import solid.lsp.accounts.before.Transfers;

public final class PredictFeeTransfer {
  public static void main(String[] args) {
    Account from = new FeeAccount(100);
    Account to = new Account(0);
    for (int i = 0; i < 3; i++) {
      Transfers.move(from, to, 10);
    }
    System.out.println(from.balance());
    System.out.println(to.balance());
    System.out.println(from.balance() + to.balance());
  }
}
// ANCHOR_END: predict-fee-transfer
