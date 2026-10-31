package solid.lsp.accounts.after;

// ANCHOR: accounts-after
import solid.lsp.accounts.step1.AccountStep1;

public final class AccountsAfterDemo {
  public static void main(String[] args) {
    try {
      new AccountStep1.Fee(1000).withdraw(100);
    } catch (IllegalStateException e) {
      System.out.println("step1: " + e.getMessage());
    }

    Account from = new Account(1000, 500);
    Account to = new Account(0);
    Account fees = new Account(0);
    System.out.println("move 600: " + Transfers.move(from, to, 600));
    System.out.println("move 100: " + Transfers.move(from, to, 100));
    System.out.println("with fee: " + Transfers.moveWithFee(from, to, fees, 100, 1));
    System.out.println("total=" + (from.balance() + to.balance() + fees.balance()));
  }
}
// ANCHOR_END: accounts-after
