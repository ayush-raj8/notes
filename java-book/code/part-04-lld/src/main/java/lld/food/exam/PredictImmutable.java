package lld.food.exam;

import java.util.List;
import java.util.Map;
import lld.food.*;

public class PredictImmutable {
  public static void main(String[] args) {
    FoodService s = new FoodService(new PricingPolicy(0, 0, 1), new NearestPartner());
    MenuItem a = new MenuItem("a", "A", 100);
    s.addRestaurant(new Restaurant("r", "R", new Location(0, 0), List.of(a)));
    Order o = s.place("c", new Location(1, 1), "r", Map.of("a", 2), null);
    try {
      o.lines().add(new OrderLine(a, 1));
    } catch (UnsupportedOperationException e) {
      System.out.println("immutable lines");
    }
    try {
      s.place("c", new Location(1, 1), "r", Map.of("a", 0), null);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    try {
      s.place("c", new Location(1, 1), "r", Map.of("zzz", 1), null);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(o.lines().get(0).totalCents());
  }
}
