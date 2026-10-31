package lld.food.exam;

import java.util.List;
import java.util.Map;
import lld.food.*;

public class PredictIllegal {
  public static void main(String[] args) {
    FoodService s = new FoodService(new PricingPolicy(0, 0, 1), new NearestPartner());
    s.addRestaurant(new Restaurant("r", "R", new Location(0, 0), List.of(new MenuItem("a", "A", 100))));
    Order o = s.place("c", new Location(1, 1), "r", Map.of("a", 1), null);
    try {
      s.advance(o.id(), OrderStatus.READY);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    s.advance(o.id(), OrderStatus.ACCEPTED);
    s.advance(o.id(), OrderStatus.PREPARING);
    try {
      s.cancel(o.id());
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(o.status());
    System.out.println(o.history());
  }
}
