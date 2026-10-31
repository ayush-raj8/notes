package patterns.structural.proxy.exam;

import patterns.structural.proxy.Account;
import patterns.structural.proxy.ProtectedAccount;
import patterns.structural.proxy.RealAccount;

// ANCHOR: predict-protection-proxy
public final class PredictProtectionProxy {
  public static void main(String[] args) {
    RealAccount real = new RealAccount(100);
    Account guest = new ProtectedAccount(real, "guest");
    System.out.println(guest.balance());
    try {
      guest.withdraw(10);
      System.out.println("withdrawn");
    } catch (SecurityException e) {
      System.out.println("denied");
    }
    real.withdraw(40);
    System.out.println(guest.balance());
  }
}
// ANCHOR_END: predict-protection-proxy
