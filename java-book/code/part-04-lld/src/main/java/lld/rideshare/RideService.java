package lld.rideshare;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Facade. One lock guards driver status, the pending queue and the ride map, so two requests can
 * never be matched to the same driver. Unmatched rides wait in FIFO order and are retried
 * whenever a driver becomes available.
 */
public final class RideService {
  private final Map<String, Driver> drivers = new TreeMap<>();
  private final Map<String, Ride> rides = new LinkedHashMap<>();
  private final Deque<Ride> pending = new ArrayDeque<>();
  private final MatchingStrategy matching;
  private final FareCalculator fares;
  private int nextId = 1;

  public RideService(MatchingStrategy matching, FareCalculator fares) {
    this.matching = matching;
    this.fares = fares;
  }

  public synchronized void registerDriver(Driver d) {
    drivers.put(d.id(), d);
  }

  public synchronized void goOnline(String driverId, Location where) {
    Driver d = driver(driverId);
    if (d.status() == DriverStatus.BUSY) {
      throw new IllegalStateException("driver is on a ride: " + driverId);
    }
    d.moveTo(where);
    d.setStatus(DriverStatus.AVAILABLE);
    dispatch();
  }

  public synchronized void goOffline(String driverId) {
    Driver d = driver(driverId);
    if (d.status() == DriverStatus.BUSY) {
      throw new IllegalStateException("driver is on a ride: " + driverId);
    }
    d.setStatus(DriverStatus.OFFLINE);
  }

  public synchronized Ride requestRide(RideRequest request) {
    Ride ride = new Ride("r-" + nextId++, request);
    rides.put(ride.id(), ride);
    pending.addLast(ride);
    dispatch();
    return ride;
  }

  public synchronized void start(String rideId) {
    ride(rideId).start();
  }

  /** Completes the trip, frees the driver at the drop-off point, returns the fare in cents. */
  public synchronized long complete(String rideId) {
    Ride ride = ride(rideId);
    long fare = fares.fareCents(ride.request());
    ride.complete(fare); // throws unless the ride is InTrip, before anything else changes
    Driver d = ((RideState.Completed) ride.state()).driver();
    d.moveTo(ride.request().drop());
    d.setStatus(DriverStatus.AVAILABLE);
    dispatch();
    return fare;
  }

  /** Allowed while waiting or matched; frees the driver if one was assigned. */
  public synchronized void cancel(String rideId, String reason) {
    Ride ride = ride(rideId);
    RideState before = ride.state();
    ride.cancel(reason);
    pending.remove(ride);
    if (before instanceof RideState.Matched m) {
      m.driver().setStatus(DriverStatus.AVAILABLE);
      dispatch();
    }
  }

  public synchronized Ride ride(String rideId) {
    Ride r = rides.get(rideId);
    if (r == null) {
      throw new IllegalArgumentException("unknown ride: " + rideId);
    }
    return r;
  }

  public synchronized List<Ride> rides() {
    return List.copyOf(rides.values());
  }

  public synchronized int pendingCount() {
    return pending.size();
  }

  private void dispatch() {
    Iterator<Ride> it = pending.iterator();
    while (it.hasNext()) {
      Ride ride = it.next();
      List<Driver> eligible =
          drivers.values().stream()
              .filter(d -> d.status() == DriverStatus.AVAILABLE && d.type() == ride.request().type())
              .toList();
      Optional<Driver> pick = matching.match(ride.request(), eligible);
      if (pick.isPresent()) {
        pick.get().setStatus(DriverStatus.BUSY);
        ride.match(pick.get());
        it.remove();
      }
    }
  }

  private Driver driver(String id) {
    Driver d = drivers.get(id);
    if (d == null) {
      throw new IllegalArgumentException("unknown driver: " + id);
    }
    return d;
  }
}
