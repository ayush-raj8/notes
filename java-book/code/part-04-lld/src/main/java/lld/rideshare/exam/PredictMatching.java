package lld.rideshare.exam;

import java.util.List;
import lld.rideshare.*;

public class PredictMatching {
  public static void main(String[] args) {
    RideRequest req =
        new RideRequest(new Rider("r", "R"), new Location(0, 0), new Location(1, 1), VehicleType.SEDAN);
    List<Driver> ds =
        List.of(
            new Driver("a", "Near", VehicleType.SEDAN, 4.1, new Location(1, 0)),
            new Driver("b", "Good", VehicleType.SEDAN, 4.9, new Location(2, 2)),
            new Driver("c", "Far", VehicleType.SEDAN, 5.0, new Location(9, 9)));
    System.out.println(new NearestDriver().match(req, ds).map(Driver::name).orElse("-"));
    System.out.println(new HighestRated(5).match(req, ds).map(Driver::name).orElse("-"));
    System.out.println(new HighestRated(0).match(req, ds).map(Driver::name).orElse("-"));
    System.out.println(new HighestRated(20).match(req, ds).map(Driver::name).orElse("-"));
  }
}
