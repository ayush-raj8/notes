package lld.rideshare;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RideShareTest {
  private RideService svc;
  private static final Location O = new Location(0, 0);

  @BeforeEach
  void setUp() {
    svc = new RideService(new NearestDriver(), FareCalculator.standard());
  }

  private Driver online(String id, VehicleType t, double rating, Location at) {
    Driver d = new Driver(id, id, t, rating, at);
    svc.registerDriver(d);
    svc.goOnline(id, at);
    return d;
  }

  private Ride request(VehicleType t, Location pickup, Location drop) {
    return svc.requestRide(new RideRequest(new Rider("r", "R"), pickup, drop, t));
  }

  @Test
  void matchesNearestAvailableDriverOfRequestedType() {
    online("far", VehicleType.SEDAN, 5, new Location(9, 9));
    online("near", VehicleType.SEDAN, 4, new Location(1, 0));
    online("bike", VehicleType.BIKE, 5, O);
    Ride r = request(VehicleType.SEDAN, O, new Location(2, 2));
    assertEquals("near", ((RideState.Matched) r.state()).driver().id());
    assertEquals(DriverStatus.BUSY, ((RideState.Matched) r.state()).driver().status());
  }

  @Test
  void noDriverMeansRequestedAndPending() {
    Ride r = request(VehicleType.SUV, O, new Location(1, 1));
    assertInstanceOf(RideState.Requested.class, r.state());
    assertEquals(1, svc.pendingCount());
  }

  @Test
  void pendingRideIsMatchedWhenDriverComesOnline() {
    Ride r = request(VehicleType.SEDAN, O, new Location(1, 1));
    online("d", VehicleType.SEDAN, 5, O);
    assertInstanceOf(RideState.Matched.class, r.state());
    assertEquals(0, svc.pendingCount());
  }

  @Test
  void pendingIsFifoAndTypeAware() {
    Ride sedanFirst = request(VehicleType.SEDAN, O, new Location(1, 1));
    Ride bike = request(VehicleType.BIKE, O, new Location(1, 1));
    online("b", VehicleType.BIKE, 5, O); // matches the bike ride even though sedan waited longer
    assertInstanceOf(RideState.Requested.class, sedanFirst.state());
    assertInstanceOf(RideState.Matched.class, bike.state());
  }

  @Test
  void completeComputesFareFreesDriverAtDropAndServesNext() {
    Driver d = online("d", VehicleType.SEDAN, 5, O);
    Ride first = request(VehicleType.SEDAN, O, new Location(3, 4));
    Ride second = request(VehicleType.SEDAN, O, new Location(1, 0));
    assertInstanceOf(RideState.Requested.class, second.state());
    svc.start(first.id());
    assertEquals(50 + 12 * 7, svc.complete(first.id()));
    assertEquals(new Location(3, 4), d.location());
    assertInstanceOf(RideState.Matched.class, second.state());
  }

  @Test
  void illegalTransitionsRejected() {
    online("d", VehicleType.SEDAN, 5, O);
    Ride r = request(VehicleType.SEDAN, O, new Location(1, 1));
    assertEquals("cannot complete from Matched", assertThrows(IllegalStateException.class, () -> svc.complete(r.id())).getMessage());
    svc.start(r.id());
    assertThrows(IllegalStateException.class, () -> svc.start(r.id()));
    assertThrows(IllegalStateException.class, () -> svc.cancel(r.id(), "late"));
  }

  @Test
  void failedCompleteDoesNotChangeDriverStatus() {
    Driver d = online("d", VehicleType.SEDAN, 5, O);
    Ride r = request(VehicleType.SEDAN, O, new Location(1, 1));
    assertThrows(IllegalStateException.class, () -> svc.complete(r.id()));
    assertEquals(DriverStatus.BUSY, d.status());
  }

  @Test
  void cancelMatchedFreesDriverAndRedispatches() {
    Driver d = online("d", VehicleType.SEDAN, 5, O);
    Ride a = request(VehicleType.SEDAN, O, new Location(1, 1));
    Ride b = request(VehicleType.SEDAN, O, new Location(1, 1));
    svc.cancel(a.id(), "changed mind");
    assertInstanceOf(RideState.Cancelled.class, a.state());
    assertInstanceOf(RideState.Matched.class, b.state());
    assertEquals(DriverStatus.BUSY, d.status());
  }

  @Test
  void cancelPendingRemovesFromQueue() {
    Ride r = request(VehicleType.SEDAN, O, new Location(1, 1));
    svc.cancel(r.id(), "x");
    assertEquals(0, svc.pendingCount());
  }

  @Test
  void busyDriverCannotGoOffline() {
    online("d", VehicleType.SEDAN, 5, O);
    request(VehicleType.SEDAN, O, new Location(1, 1));
    assertThrows(IllegalStateException.class, () -> svc.goOffline("d"));
  }

  @Test
  void offlineDriverIsNotMatched() {
    online("d", VehicleType.SEDAN, 5, O);
    svc.goOffline("d");
    assertInstanceOf(RideState.Requested.class, request(VehicleType.SEDAN, O, O).state());
  }

  @Test
  void highestRatedStrategyRespectsRadius() {
    RideService s = new RideService(new HighestRated(3), FareCalculator.standard());
    s.registerDriver(new Driver("star", "S", VehicleType.SEDAN, 5.0, new Location(9, 9)));
    s.registerDriver(new Driver("ok", "O", VehicleType.SEDAN, 4.0, new Location(1, 1)));
    s.goOnline("star", new Location(9, 9));
    s.goOnline("ok", new Location(1, 1));
    Ride r = s.requestRide(new RideRequest(new Rider("r", "R"), O, new Location(1, 0), VehicleType.SEDAN));
    assertEquals("ok", ((RideState.Matched) r.state()).driver().id());
  }

  @Test
  void surgeDecoratesFare() {
    RideRequest r = new RideRequest(new Rider("r", "R"), O, new Location(0, 10), VehicleType.BIKE);
    long base = FareCalculator.standard().fareCents(r); // 20 + 8*10 = 100
    assertEquals(100, base);
    assertEquals(250, new SurgeFare(FareCalculator.standard(), 250).fareCents(r));
    assertThrows(IllegalArgumentException.class, () -> new SurgeFare(FareCalculator.standard(), 99));
  }

  @Test
  void unknownIdsRejected() {
    assertThrows(IllegalArgumentException.class, () -> svc.start("r-99"));
    assertThrows(IllegalArgumentException.class, () -> svc.goOnline("ghost", O));
  }

  @Test
  void concurrentRequestsNeverShareADriver() throws Exception {
    for (int i = 0; i < 5; i++) {
      online("d" + i, VehicleType.SEDAN, 5, new Location(i, i));
    }
    int requests = 40;
    ExecutorService pool = Executors.newFixedThreadPool(8);
    CountDownLatch done = new CountDownLatch(requests);
    List<Ride> rides = java.util.Collections.synchronizedList(new ArrayList<>());
    for (int i = 0; i < requests; i++) {
      pool.execute(() -> {
        rides.add(request(VehicleType.SEDAN, O, new Location(1, 1)));
        done.countDown();
      });
    }
    assertTrue(done.await(10, TimeUnit.SECONDS));
    pool.shutdown();
    List<String> assigned = new ArrayList<>();
    for (Ride r : rides) {
      if (r.state() instanceof RideState.Matched m) {
        assigned.add(m.driver().id());
      }
    }
    assertEquals(5, assigned.size());
    assertEquals(5, assigned.stream().distinct().count());
    assertEquals(35, svc.pendingCount());
  }

  @Test
  void describeCoversEveryState() {
    Driver d = new Driver("d", "D", VehicleType.BIKE, 5, O);
    assertEquals("cancelled: x", RideState.describe(new RideState.Cancelled("x")));
    assertEquals("done, fare 9", RideState.describe(new RideState.Completed(d, 9)));
  }
}
