package solid.lsp.accounts.before;

// ANCHOR: accounts-failure
public final class AccountsFailureDemo {
  public static void main(String[] args) {
    Account plain = new Account(1000);
    Account sink = new Account(0);
    Transfers.move(plain, sink, 100);
    System.out.println("plain  total=" + (plain.balance() + sink.balance()));

    Account fee = new FeeAccount(1000);
    sink = new Account(0);
    Transfers.move(fee, sink, 100);
    System.out.println("fee    total=" + (fee.balance() + sink.balance()));

    Account capped = new CappedAccount(1000);
    try {
      Transfers.move(capped, new Account(0), 600);
    } catch (IllegalArgumentException e) {
      System.out.println("capped: " + e.getMessage());
    }
  }
}
// ANCHOR_END: accounts-failure
