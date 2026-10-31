package solid.lsp.accounts.before;

// ANCHOR: transfers
public final class Transfers {
  /** Written against Account's contract. Total money must not change. */
  public static void move(Account from, Account to, long amount) {
    from.withdraw(amount);
    to.deposit(amount);
  }
}
// ANCHOR_END: transfers
