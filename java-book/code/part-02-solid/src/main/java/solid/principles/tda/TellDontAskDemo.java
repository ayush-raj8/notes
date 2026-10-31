package solid.principles.tda;

public final class TellDontAskDemo {
  // ANCHOR: tda-before
  /** Asks, then decides, then mutates: the rule lives in every caller. */
  static void checkoutCareful(LooseWallet w, long price) {
    if (w.getBalance() >= price) {
      w.setBalance(w.getBalance() - price);
    }
  }

  /** A second caller, written later, that forgot the check. */
  static void checkoutForgetful(LooseWallet w, long price) {
    w.setBalance(w.getBalance() - price);
  }
  // ANCHOR_END: tda-before

  // ANCHOR: tda-after
  static boolean checkout(Wallet w, long price) {
    return w.spend(price);
  }
  // ANCHOR_END: tda-after

  public static void main(String[] args) {
    // ANCHOR: tda-main
    LooseWallet a = new LooseWallet(100);
    checkoutCareful(a, 150);
    System.out.println("careful   balance=" + a.getBalance());
    LooseWallet b = new LooseWallet(100);
    checkoutForgetful(b, 150);
    System.out.println("forgetful balance=" + b.getBalance());

    Wallet w = new Wallet(100);
    System.out.println("tell      spent=" + checkout(w, 150) + " balance=" + w.balance());
    // ANCHOR_END: tda-main
  }
}
