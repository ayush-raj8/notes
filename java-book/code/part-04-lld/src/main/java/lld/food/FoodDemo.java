package lld.food;

import java.util.List;
import java.util.Map;

public final class FoodDemo {
  public static void main(String[] args) {
    FoodService svc = new FoodService(new PricingPolicy(30, 10, 2000), new NearestPartner());
    svc.addRestaurant(
        new Restaurant(
            "r1",
            "Spice Hut",
            new Location(0, 0),
            List.of(new MenuItem("dosa", "Dosa", 300), new MenuItem("chai", "Chai", 150))));
    svc.addPartner(new DeliveryPartner("p1", "Ravi", new Location(5, 5)));
    svc.addPartner(new DeliveryPartner("p2", "Sita", new Location(1, 2)));
    svc.addCoupon(new Coupon("SAVE10", 10, 100));

    Order o = svc.place("c1", new Location(2, 1), "r1", Map.of("dosa", 2, "chai", 1), "SAVE10");
    System.out.println(o.id() + " subtotal=" + o.subtotalCents() + " fee=" + o.deliveryFeeCents()
        + " discount=" + o.discountCents() + " total=" + o.totalCents());
    for (OrderStatus s : List.of(OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY)) {
      svc.advance(o.id(), s);
    }
    System.out.println("partner " + o.partnerId());
    svc.advance(o.id(), OrderStatus.PICKED_UP);
    svc.advance(o.id(), OrderStatus.DELIVERED);
    System.out.println(o.history());
    try {
      svc.cancel(o.id());
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
