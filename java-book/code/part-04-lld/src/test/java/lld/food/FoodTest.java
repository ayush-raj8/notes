package lld.food;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FoodTest {
  private FoodService svc;

  @BeforeEach
  void setUp() {
    svc = new FoodService(new PricingPolicy(30, 10, 2000), new NearestPartner());
    svc.addRestaurant(
        new Restaurant(
            "r1",
            "Spice Hut",
            new Location(0, 0),
            List.of(new MenuItem("dosa", "Dosa", 300), new MenuItem("chai", "Chai", 150))));
    svc.addCoupon(new Coupon("SAVE10", 10, 100));
  }

  private Order place(Map<String, Integer> items, String coupon) {
    return svc.place("c1", new Location(2, 1), "r1", items, coupon);
  }

  private void toReady(Order o) {
    svc.advance(o.id(), OrderStatus.ACCEPTED);
    svc.advance(o.id(), OrderStatus.PREPARING);
    svc.advance(o.id(), OrderStatus.READY);
  }

  @Test
  void totalsAreSubtotalPlusFeeMinusDiscount() {
    Order o = place(Map.of("dosa", 2, "chai", 1), "SAVE10");
    assertEquals(750, o.subtotalCents());
    assertEquals(60, o.deliveryFeeCents());
    assertEquals(75, o.discountCents());
    assertEquals(735, o.totalCents());
  }

  @Test
  void couponIsCapped() {
    Order o = place(Map.of("dosa", 10), "SAVE10"); // 3000 -> 10% = 300, cap 100
    assertEquals(100, o.discountCents());
  }

  @Test
  void freeDeliveryAtThreshold() {
    assertEquals(0, place(Map.of("dosa", 7), null).deliveryFeeCents()); // 2100 >= 2000
    assertEquals(60, place(Map.of("dosa", 6), null).deliveryFeeCents()); // 1800 < 2000
  }

  @Test
  void priceComesFromMenuNotCaller() {
    assertEquals(300, place(Map.of("dosa", 1), null).lines().get(0).item().priceCents());
  }

  @Test
  void linesAreSortedAndImmutable() {
    Order o = place(Map.of("dosa", 1, "chai", 1), null);
    assertEquals("chai", o.lines().get(0).item().id());
    assertThrows(UnsupportedOperationException.class, () -> o.lines().clear());
  }

  @Test
  void invalidOrdersRejected() {
    assertThrows(IllegalArgumentException.class, () -> place(Map.of(), null));
    assertThrows(IllegalArgumentException.class, () -> place(Map.of("dosa", 0), null));
    assertThrows(IllegalArgumentException.class, () -> place(Map.of("nope", 1), null));
    assertThrows(IllegalArgumentException.class, () -> place(Map.of("dosa", 1), "BAD"));
    assertThrows(
        IllegalArgumentException.class,
        () -> svc.place("c", new Location(0, 0), "zz", Map.of("dosa", 1), null));
  }

  @Test
  void happyPathHistory() {
    svc.addPartner(new DeliveryPartner("p1", "Ravi", new Location(1, 1)));
    Order o = place(Map.of("dosa", 1), null);
    toReady(o);
    svc.advance(o.id(), OrderStatus.PICKED_UP);
    svc.advance(o.id(), OrderStatus.DELIVERED);
    assertEquals(
        List.of(
            OrderStatus.PLACED,
            OrderStatus.ACCEPTED,
            OrderStatus.PREPARING,
            OrderStatus.READY,
            OrderStatus.PICKED_UP,
            OrderStatus.DELIVERED),
        o.history());
  }

  @Test
  void illegalTransitionsRejected() {
    Order o = place(Map.of("dosa", 1), null);
    assertThrows(IllegalStateException.class, () -> svc.advance(o.id(), OrderStatus.READY));
    svc.advance(o.id(), OrderStatus.ACCEPTED);
    svc.advance(o.id(), OrderStatus.PREPARING);
    assertThrows(IllegalStateException.class, () -> svc.cancel(o.id()));
  }

  @Test
  void cancelAllowedEarlyAndFinal() {
    Order o = place(Map.of("dosa", 1), null);
    svc.cancel(o.id());
    assertEquals(OrderStatus.CANCELLED, o.status());
    assertThrows(IllegalStateException.class, () -> svc.advance(o.id(), OrderStatus.ACCEPTED));
  }

  @Test
  void everyStatusAgreesWithItsNextSet() {
    for (OrderStatus from : OrderStatus.values()) {
      for (OrderStatus to : OrderStatus.values()) {
        assertEquals(from.next().contains(to), from.canGoTo(to));
      }
    }
    assertTrue(OrderStatus.DELIVERED.isTerminal());
    assertFalse(OrderStatus.READY.isTerminal());
  }

  @Test
  void nearestPartnerWithDeterministicTieBreak() {
    svc.addPartner(new DeliveryPartner("p1", "Far", new Location(5, 5)));
    svc.addPartner(new DeliveryPartner("p3", "TieB", new Location(2, 1)));
    svc.addPartner(new DeliveryPartner("p2", "TieA", new Location(1, 2)));
    Order o = place(Map.of("dosa", 1), null);
    toReady(o);
    assertEquals("p2", o.partnerId());
  }

  @Test
  void pickUpWithoutPartnerRejectedAndStateKept() {
    Order o = place(Map.of("dosa", 1), null);
    toReady(o); // no partners registered
    assertNull(o.partnerId());
    assertThrows(IllegalStateException.class, () -> svc.advance(o.id(), OrderStatus.PICKED_UP));
    assertEquals(OrderStatus.READY, o.status());
  }

  @Test
  void waitingOrderGetsPartnerWhenOneIsFreed() {
    svc.addPartner(new DeliveryPartner("p1", "Solo", new Location(0, 0)));
    Order first = place(Map.of("dosa", 1), null);
    Order second = place(Map.of("dosa", 1), null);
    toReady(first);
    toReady(second);
    assertEquals("p1", first.partnerId());
    assertNull(second.partnerId());
    svc.advance(first.id(), OrderStatus.PICKED_UP);
    svc.advance(first.id(), OrderStatus.DELIVERED);
    assertEquals("p1", second.partnerId());
  }

  @Test
  void waitingIsFifo() {
    svc.addPartner(new DeliveryPartner("p1", "Solo", new Location(0, 0)));
    Order a = place(Map.of("dosa", 1), null);
    Order b = place(Map.of("dosa", 1), null);
    Order c = place(Map.of("dosa", 1), null);
    toReady(a);
    toReady(b);
    toReady(c);
    svc.advance(a.id(), OrderStatus.PICKED_UP);
    svc.advance(a.id(), OrderStatus.DELIVERED);
    assertEquals("p1", b.partnerId());
    assertNull(c.partnerId());
  }

  @Test
  void deliveringMovesPartnerToDestination() {
    DeliveryPartner p = new DeliveryPartner("p1", "Ravi", new Location(0, 0));
    svc.addPartner(p);
    Order o = place(Map.of("dosa", 1), null);
    toReady(o);
    svc.advance(o.id(), OrderStatus.PICKED_UP);
    svc.advance(o.id(), OrderStatus.DELIVERED);
    assertEquals(new Location(2, 1), p.location());
    assertTrue(p.isAvailable());
  }

  @Test
  void customStrategyViaLambda() {
    FoodService s = new FoodService(new PricingPolicy(0, 0, 1), (r, free) -> free.stream().reduce((a, b) -> b));
    s.addRestaurant(new Restaurant("r", "R", new Location(0, 0), List.of(new MenuItem("a", "A", 100))));
    s.addPartner(new DeliveryPartner("p1", "One", new Location(0, 0)));
    s.addPartner(new DeliveryPartner("p2", "Two", new Location(9, 9)));
    Order o = s.place("c", new Location(1, 1), "r", Map.of("a", 1), null);
    s.advance(o.id(), OrderStatus.ACCEPTED);
    s.advance(o.id(), OrderStatus.PREPARING);
    s.advance(o.id(), OrderStatus.READY);
    assertEquals("p2", o.partnerId());
  }

  @Test
  void validationOfValueTypes() {
    assertThrows(IllegalArgumentException.class, () -> new Coupon("X", 0, 10));
    assertThrows(IllegalArgumentException.class, () -> new MenuItem("a", "A", 0));
    assertThrows(IllegalArgumentException.class, () -> new OrderLine(new MenuItem("a", "A", 1), 0));
  }
}
