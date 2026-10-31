package lld.rideshare.exam;

import lld.rideshare.*;

public class PredictFare {
  public static void main(String[] args) {
    RideRequest r =
        new RideRequest(new Rider("r", "R"), new Location(0, 0), new Location(3, 4), VehicleType.SEDAN);
    System.out.println(r.distanceKm());
    System.out.println(FareCalculator.standard().fareCents(r));
    System.out.println(new SurgeFare(FareCalculator.standard(), 150).fareCents(r));
    System.out.println(new SurgeFare(FareCalculator.standard(), 105).fareCents(r));
    try {
      new SurgeFare(FareCalculator.standard(), 90);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
