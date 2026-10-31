package solid.lsp.exam;

// ANCHOR: predict-postcondition
import solid.lsp.accounts.step1.AccountStep1;

public final class PredictPostcondition {
  public static void main(String[] args) {
    AccountStep1 acc = new AccountStep1.Fee(1000);
    try {
      acc.withdraw(100);
      System.out.println("ok");
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(acc.balance());
  }
}
// ANCHOR_END: predict-postcondition
