package patterns.structural.proxy.exam;

import java.util.ArrayList;
import java.util.List;
import patterns.structural.proxy.Account;
import patterns.structural.proxy.LoggingProxies;
import patterns.structural.proxy.ProtectedAccount;
import patterns.structural.proxy.RealAccount;

// ANCHOR: predict-proxy-exception
public final class PredictProxyException {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    Account inner = new ProtectedAccount(new RealAccount(5), "guest");
    Account a = LoggingProxies.wrap(Account.class, inner, log);
    try {
      a.withdraw(1);
    } catch (SecurityException e) {
      System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
    }
    System.out.println(log);
  }
}
// ANCHOR_END: predict-proxy-exception
