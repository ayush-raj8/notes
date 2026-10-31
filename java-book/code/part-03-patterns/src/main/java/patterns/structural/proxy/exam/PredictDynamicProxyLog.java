package patterns.structural.proxy.exam;

import java.util.ArrayList;
import java.util.List;
import patterns.structural.proxy.Account;
import patterns.structural.proxy.LoggingProxies;
import patterns.structural.proxy.RealAccount;

// ANCHOR: predict-dynamic-proxy-log
public final class PredictDynamicProxyLog {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    Account a = LoggingProxies.wrap(Account.class, new RealAccount(10), log);
    a.withdraw(3);
    a.withdraw(2);
    int b = a.balance();
    System.out.println(b);
    System.out.println(log);
    System.out.println(log.size());
  }
}
// ANCHOR_END: predict-dynamic-proxy-log
