package lld.rideshare.exam;

import lld.rideshare.*;

public class PredictPending {
  static String name(Ride r) {
    return r.state().getClass().getSimpleName();
  }

  public static void main(String[] args) {
    RideService s = new RideService(new NearestDriver(), FareCalculator.standard());
    s.registerDriver(new Driver("d", "D", VehicleType.SEDAN, 5, new Location(0, 0)));
    s.registerDriver(new Driver("m", "M", VehicleType.BIKE, 5, new Location(0, 0)));
    s.goOnline("m", new Location(0, 0));
    RideRequest sedan = new RideRequest(new Rider("r", "R"), new Location(0, 0), new Location(1, 0), VehicleType.SEDAN);
    Ride r1 = s.requestRide(sedan);
    System.out.println(name(r1) + " " + s.pendingCount());
    s.goOnline("d", new Location(0, 0));
    System.out.println(name(r1) + " " + s.pendingCount());
    Ride r2 = s.requestRide(sedan);
    System.out.println(name(r2) + " " + s.pendingCount());
    s.start(r1.id());
    s.complete(r1.id());
    System.out.println(name(r2) + " " + s.pendingCount());
  }
}
