package lld.rideshare.exam;

import lld.rideshare.*;

public class PredictTransitions {
  public static void main(String[] args) {
    RideService s = new RideService(new NearestDriver(), FareCalculator.standard());
    s.registerDriver(new Driver("d", "D", VehicleType.BIKE, 5, new Location(0, 0)));
    s.goOnline("d", new Location(0, 0));
    Ride r = s.requestRide(new RideRequest(new Rider("r", "R"), new Location(0, 1), new Location(0, 3), VehicleType.BIKE));
    try {
      s.complete(r.id());
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    s.start(r.id());
    try {
      s.start(r.id());
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    try {
      s.cancel(r.id(), "late");
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(s.complete(r.id()));
    System.out.println(RideState.describe(r.state()));
  }
}
