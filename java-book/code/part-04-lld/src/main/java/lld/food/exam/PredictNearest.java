package lld.food.exam;

import java.util.List;
import lld.food.*;

public class PredictNearest {
  public static void main(String[] args) {
    Restaurant r = new Restaurant("r", "R", new Location(0, 0), List.of());
    List<DeliveryPartner> free =
        List.of(
            new DeliveryPartner("p1", "Far", new Location(5, 5)),
            new DeliveryPartner("p3", "TieB", new Location(2, 1)),
            new DeliveryPartner("p2", "TieA", new Location(1, 2)));
    System.out.println(new NearestPartner().pick(r, free).map(DeliveryPartner::name).orElse("none"));
    System.out.println(new NearestPartner().pick(r, List.of()).isPresent());
    System.out.println(new Location(1, 2).distanceTo(new Location(4, -2)));
  }
}
