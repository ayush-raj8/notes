package lld.food.exam;

import java.util.List;
import java.util.Map;
import lld.food.*;

public class PredictTotals {
  public static void main(String[] args) {
    FoodService s = new FoodService(new PricingPolicy(30, 10, 2000), new NearestPartner());
    s.addRestaurant(new Restaurant("r", "R", new Location(0, 0),
        List.of(new MenuItem("a", "A", 300), new MenuItem("b", "B", 150))));
    s.addCoupon(new Coupon("SAVE10", 10, 100));
    Order o = s.place("c", new Location(2, 1), "r", Map.of("a", 2, "b", 1), "SAVE10");
    System.out.println(o.subtotalCents());
    System.out.println(o.deliveryFeeCents());
    System.out.println(o.discountCents());
    System.out.println(o.totalCents());
    Order big = s.place("c", new Location(9, 9), "r", Map.of("a", 8), "SAVE10");
    System.out.println(big.deliveryFeeCents() + " " + big.discountCents() + " " + big.totalCents());
  }
}
