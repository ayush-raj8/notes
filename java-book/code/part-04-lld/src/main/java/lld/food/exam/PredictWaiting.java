package lld.food.exam;

import java.util.List;
import java.util.Map;
import lld.food.*;

public class PredictWaiting {
  static void ready(FoodService s, String id) {
    s.advance(id, OrderStatus.ACCEPTED);
    s.advance(id, OrderStatus.PREPARING);
    s.advance(id, OrderStatus.READY);
  }

  public static void main(String[] args) {
    FoodService s = new FoodService(new PricingPolicy(0, 0, 1), new NearestPartner());
    s.addRestaurant(new Restaurant("r", "R", new Location(0, 0), List.of(new MenuItem("a", "A", 100))));
    s.addPartner(new DeliveryPartner("p1", "Solo", new Location(0, 0)));
    Order first = s.place("c", new Location(1, 1), "r", Map.of("a", 1), null);
    Order second = s.place("c", new Location(1, 1), "r", Map.of("a", 1), null);
    ready(s, first.id());
    ready(s, second.id());
    System.out.println(first.partnerId() + " " + second.partnerId());
    s.advance(first.id(), OrderStatus.PICKED_UP);
    s.advance(first.id(), OrderStatus.DELIVERED);
    System.out.println(first.partnerId() + " " + second.partnerId());
    try {
      s.advance("o-9", OrderStatus.ACCEPTED);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
