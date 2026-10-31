package lld.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

// ANCHOR: parking-test
class ParkingLotTest {
  private static Vehicle car(String plate) {
    return new Vehicle(plate, VehicleType.CAR);
  }

  @Test
  void bestFitKeepsLargeSpotForTrucks() throws Exception {
    ParkingLot lot = ParkingDemo.smallLot(new BestFitStrategy());
    assertEquals("F0-S1", lot.enter(new Vehicle("b", VehicleType.MOTORCYCLE), 0).spotId());
    assertEquals("F0-L1", lot.enter(new Vehicle("t", VehicleType.TRUCK), 0).spotId());
  }

  @Test
  void firstFitTakesNearestFittingSpot() throws Exception {
    ParkingLot lot = ParkingDemo.smallLot(new FirstFitStrategy());
    assertEquals("F0-L1", lot.enter(new Vehicle("b", VehicleType.MOTORCYCLE), 0).spotId());
  }

  @Test
  void feeRoundsUpStartedHours() throws Exception {
    ParkingLot lot = ParkingDemo.smallLot(new BestFitStrategy());
    Ticket t = lot.enter(car("c"), 100);
    assertEquals(40, lot.exit(t.id(), 100 + 61));
  }

  @Test
  void lotFullThenSpaceFreedAfterExit() throws Exception {
    ParkingLot lot = ParkingDemo.smallLot(new BestFitStrategy());
    Ticket truck = lot.enter(new Vehicle("t1", VehicleType.TRUCK), 0);
    assertThrows(LotFullException.class, () -> lot.enter(new Vehicle("t2", VehicleType.TRUCK), 1));
    lot.exit(truck.id(), 5);
    assertEquals("F0-L1", lot.enter(new Vehicle("t2", VehicleType.TRUCK), 6).spotId());
  }

  @Test
  void ticketCannotBeUsedTwice() throws Exception {
    ParkingLot lot = ParkingDemo.smallLot(new BestFitStrategy());
    Ticket t = lot.enter(car("c"), 0);
    lot.exit(t.id(), 10);
    assertThrows(IllegalArgumentException.class, () -> lot.exit(t.id(), 20));
    assertEquals(3, lot.freeSpots());
  }

  @Test
  void samePlateCannotEnterTwice() throws Exception {
    ParkingLot lot = ParkingDemo.smallLot(new BestFitStrategy());
    lot.enter(car("c"), 0);
    assertThrows(IllegalStateException.class, () -> lot.enter(car("c"), 1));
  }

  @Test
  void concurrentEntriesNeverOversell() throws Exception {
    List<ParkingSpot> spots = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      spots.add(new ParkingSpot("S" + i, 0, SpotSize.MEDIUM));
    }
    ParkingLot lot = new ParkingLot(spots, new FirstFitStrategy(), new HourlyFeePolicy(1, 1, 1));
    int threads = 40;
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    CountDownLatch go = new CountDownLatch(1);
    AtomicInteger accepted = new AtomicInteger();
    for (int i = 0; i < threads; i++) {
      String plate = "P" + i;
      pool.submit(() -> {
        go.await();
        try {
          lot.enter(car(plate), 0);
          accepted.incrementAndGet();
        } catch (LotFullException ignored) {
          // expected for the losers
        }
        return null;
      });
    }
    go.countDown();
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    assertEquals(10, accepted.get());
    assertEquals(0, lot.freeSpots());
  }
}
// ANCHOR_END: parking-test
