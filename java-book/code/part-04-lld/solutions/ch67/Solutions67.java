import java.util.*;
import lld.rideshare.*;

/** Exercise solutions for chapter 67. */
public class Solutions67 {
  /** Ex 1: a lambda strategy — lowest driver id wins. */
  static final MatchingStrategy LOWEST_ID =
      (request, drivers) -> drivers.stream().min(Comparator.comparing(Driver::id));

  /** Ex 2: another decorator — add a flat fee on top of any calculator. */
  static final class FlatFee implements FareCalculator {
    private final FareCalculator inner;
    private final long flatCents;

    FlatFee(FareCalculator inner, long flatCents) {
      this.inner = inner;
      this.flatCents = flatCents;
    }

    @Override
    public long fareCents(RideRequest request) {
      return inner.fareCents(request) + flatCents;
    }
  }

  /** Ex 3: terminal check with an exhaustive switch (no default). */
  static boolean isFinished(RideState s) {
    return switch (s) {
      case RideState.Completed c -> true;
      case RideState.Cancelled c -> true;
      case RideState.Requested r -> false;
      case RideState.Matched m -> false;
      case RideState.InTrip t -> false;
    };
  }

  /** Ex 4: price quote for every vehicle type, in declaration order. */
  static Map<VehicleType, Long> quote(Location pickup, Location drop) {
    Map<VehicleType, Long> out = new EnumMap<>(VehicleType.class);
    for (VehicleType t : VehicleType.values()) {
      out.put(t, FareCalculator.standard().fareCents(new RideRequest(new Rider("q", "Q"), pickup, drop, t)));
    }
    return out;
  }

  /** Ex 5: how many rides ended in each state kind. */
  static Map<String, Integer> byState(List<Ride> rides) {
    Map<String, Integer> counts = new TreeMap<>();
    for (Ride r : rides) {
      counts.merge(r.state().getClass().getSimpleName(), 1, Integer::sum);
    }
    return counts;
  }

  public static void main(String[] args) {
    List<Driver> ds = List.of(new Driver("b", "B", VehicleType.SEDAN, 4, new Location(0, 0)), new Driver("a", "A", VehicleType.SEDAN, 4, new Location(5, 5)));
    RideRequest req = new RideRequest(new Rider("r", "R"), new Location(0, 0), new Location(2, 2), VehicleType.SEDAN);
    System.out.println("ex1 " + LOWEST_ID.match(req, ds).map(Driver::id).orElse("-"));
    System.out.println("ex2 " + new FlatFee(FareCalculator.standard(), 30).fareCents(req));
    System.out.println("ex3 " + isFinished(new RideState.Cancelled("x")) + " " + isFinished(new RideState.Requested()));
    System.out.println("ex4 " + quote(new Location(0, 0), new Location(2, 3)));

    RideService svc = new RideService(LOWEST_ID, FareCalculator.standard());
    svc.registerDriver(ds.get(0));
    svc.goOnline("b", new Location(0, 0));
    Ride one = svc.requestRide(req);
    svc.requestRide(req);
    svc.cancel(one.id(), "test");
    System.out.println("ex5 " + byState(svc.rides()));
  }
}
