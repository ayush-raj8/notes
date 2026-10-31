package patterns.structural.proxy;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: proxy-demo
public final class ProxyDemo {
  public static void main(String[] args) {
    Image img = new ImageProxy("cat.png");
    System.out.println("loads=" + RealImage.loads());
    System.out.println(img.display());
    System.out.println(img.display());
    System.out.println("loads=" + RealImage.loads());

    Account owner = new ProtectedAccount(new RealAccount(100), "owner");
    Account guest = new ProtectedAccount(new RealAccount(100), "guest");
    owner.withdraw(30);
    System.out.println(owner.balance());
    try {
      guest.withdraw(30);
    } catch (SecurityException e) {
      System.out.println(e.getMessage());
    }

    List<String> log = new ArrayList<>();
    Account logged = LoggingProxies.wrap(Account.class, new RealAccount(50), log);
    logged.withdraw(10);
    System.out.println(logged.balance());
    System.out.println(log);
  }
}
// ANCHOR_END: proxy-demo
