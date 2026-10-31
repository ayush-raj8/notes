package solid.principles.exam;

// ANCHOR: predict-wallet-rules
import solid.principles.tda.LooseWallet;
import solid.principles.tda.Wallet;

public final class PredictWalletRules {
  public static void main(String[] args) {
    LooseWallet loose = new LooseWallet(100);
    loose.setBalance(loose.getBalance() - 130);
    System.out.println(loose.getBalance());

    Wallet w = new Wallet(100);
    System.out.println(w.spend(130));
    System.out.println(w.spend(60));
    System.out.println(w.spend(60));
    System.out.println(w.balance());
  }
}
// ANCHOR_END: predict-wallet-rules
