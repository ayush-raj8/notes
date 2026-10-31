package lld.rideshare;

public final class RideShareDemo {
  public static void main(String[] args) {
    RideService svc = new RideService(new NearestDriver(), new SurgeFare(FareCalculator.standard(), 150));
    svc.registerDriver(new Driver("d1", "Asha", VehicleType.SEDAN, 4.9, new Location(0, 0)));
    svc.registerDriver(new Driver("d2", "Ben", VehicleType.SEDAN, 4.2, new Location(3, 3)));
    svc.goOnline("d1", new Location(0, 0));
    svc.goOnline("d2", new Location(3, 3));

    Location pickup = new Location(1, 1);
    Location drop = new Location(4, 5);
    Ride a = svc.requestRide(new RideRequest(new Rider("r1", "Cy"), pickup, drop, VehicleType.SEDAN));
    Ride b = svc.requestRide(new RideRequest(new Rider("r2", "Di"), pickup, drop, VehicleType.SEDAN));
    Ride c = svc.requestRide(new RideRequest(new Rider("r3", "Ed"), pickup, drop, VehicleType.SEDAN));
    for (Ride r : svc.rides()) {
      System.out.println(r.id() + ": " + RideState.describe(r.state()));
    }
    svc.start(a.id());
    System.out.println("fare " + svc.complete(a.id()));
    System.out.println(c.id() + ": " + RideState.describe(c.state()));
    svc.cancel(b.id(), "rider changed mind");
    System.out.println(b.id() + ": " + RideState.describe(b.state()));
    System.out.println("pending " + svc.pendingCount());
  }
}
