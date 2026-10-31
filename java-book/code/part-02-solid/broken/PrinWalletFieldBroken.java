package solid.principles;

import solid.principles.tda.Wallet;

/** Wallet owns balance: callers cannot reach in, so they cannot forget the rule. */
public final class PrinWalletFieldBroken {
  public static void main(String[] args) {
    Wallet w = new Wallet(100);
    w.balance -= 150;
  }
}
