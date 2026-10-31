package patterns.structural.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import patterns.structural.facade.subsystem.HomeTheaterFacade;

class FacadeTest {
  @Test
  void theaterFacadeOrdersSubsystemCalls() {
    var log = new HomeTheaterFacade().watchMovie("Up");
    assertEquals("lights 10%", log.get(0));
    assertEquals("playing Up", log.get(log.size() - 1));
  }

  @Test
  void failedPaymentReleasesStock() {
    Inventory inv = new Inventory();
    OrderFacade f = new OrderFacade(inv, new Payment(), new Shipping());
    assertEquals("FAILED: limit exceeded: 20000", f.place("pen", 20_000));
    assertEquals(1, inv.left("pen"));
    assertEquals("OK TRK-PEN", f.place("pen", 100));
  }

  @Test
  void outOfStock() {
    OrderFacade f = new OrderFacade(new Inventory(), new Payment(), new Shipping());
    assertEquals("FAILED: out of stock cap", f.place("cap", 1));
  }
}
