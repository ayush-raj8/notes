package patterns.structural.proxy.exam;

import java.util.ArrayList;
import patterns.structural.proxy.Account;
import patterns.structural.proxy.LoggingProxies;
import patterns.structural.proxy.RealAccount;

// ANCHOR: predict-proxy-cast
public final class PredictProxyCast {
  public static void main(String[] args) {
    Account p = LoggingProxies.wrap(Account.class, new RealAccount(1), new ArrayList<>());
    System.out.println(p instanceof Account);
    System.out.println(p instanceof RealAccount);
    try {
      RealAccount r = (RealAccount) p;
      System.out.println("cast ok");
    } catch (ClassCastException e) {
      System.out.println("CCE");
    }
  }
}
// ANCHOR_END: predict-proxy-cast
