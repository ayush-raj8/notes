package solid.lsp.accounts.after;

// ANCHOR: transfers
public final class Transfers {
  /** Step 2: ask first, then move. Returns false when the precondition does not hold. */
  public static boolean move(Account from, Account to, long amount) {
    if (!from.canWithdraw(amount)) {
      return false;
    }
    from.withdraw(amount);
    to.deposit(amount);
    return true;
  }

  /** Step 3: a fee is a second, visible movement into a fee account; total money is conserved. */
  public static boolean moveWithFee(Account from, Account to, Account fees, long amount, long fee) {
    if (!from.canWithdraw(amount + fee)) {
      return false;
    }
    from.withdraw(amount + fee);
    to.deposit(amount);
    fees.deposit(fee);
    return true;
  }
}
// ANCHOR_END: transfers
