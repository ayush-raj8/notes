package solid.lsp.exam;

// ANCHOR: predict-capped-transfer
import solid.lsp.accounts.before.Account;
import solid.lsp.accounts.before.CappedAccount;
import solid.lsp.accounts.before.Transfers;

public final class PredictCappedTransfer {
  public static void main(String[] args) {
    Account from = new CappedAccount(1000);
    Account to = new Account(0);
    Transfers.move(from, to, 500);
    System.out.println(from.balance() + " " + to.balance());
    try {
      Transfers.move(from, to, 501);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(from.balance() + " " + to.balance());
  }
}
// ANCHOR_END: predict-capped-transfer
