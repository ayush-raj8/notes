package patterns.structural.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProxyTest {
  @Test
  void virtualProxyLoadsOnFirstUse() {
    RealImage.resetLoads();
    Image p = new ImageProxy("x");
    assertEquals(0, RealImage.loads());
    p.display();
    p.display();
    assertEquals(1, RealImage.loads());
  }

  @Test
  void protectionProxyDeniesGuest() {
    Account g = new ProtectedAccount(new RealAccount(10), "guest");
    assertThrows(SecurityException.class, () -> g.withdraw(1));
    assertEquals(10, g.balance());
  }

  @Test
  void dynamicProxyLogsAndUnwrapsExceptions() {
    List<String> log = new ArrayList<>();
    Account a = LoggingProxies.wrap(Account.class, new ProtectedAccount(new RealAccount(5), "guest"), log);
    assertTrue(Proxy.isProxyClass(a.getClass()));
    assertThrows(SecurityException.class, () -> a.withdraw(1));
    assertEquals(List.of("withdraw"), log);
  }
}
