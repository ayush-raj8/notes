import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lld.parking.*;

/** Exercises 1-5 of chapter 51. */
public class SolutionParking {
  // Ex 1: first 15 minutes are free; FeePolicy has one abstract method, so a lambda works.
  static FeePolicy graceMinutes(FeePolicy base, long grace) {
    return (type, minutes) -> minutes <= grace ? 0 : base.fee(type, minutes);
  }

  // Ex 2: lowest floor first.
  static final SpotAllocationStrategy LOWEST_FLOOR =
      fitting -> fitting.stream().min(Comparator.comparingInt(ParkingSpot::floor));

  // Ex 3: a gate turns the checked exception into a message.
  static String gate(ParkingLot lot, Vehicle v, long now) {
    try {
      return "ticket " + lot.enter(v, now).id();
    } catch (LotFullException e) {
      return "FULL: " + e.getMessage();
    }
  }

  public static void main(String[] args) throws Exception {
    FeePolicy grace = graceMinutes(new HourlyFeePolicy(10, 20, 50), 15);
    System.out.println(grace.fee(VehicleType.CAR, 15) + " " + grace.fee(VehicleType.CAR, 16));

    ParkingLot lot = new ParkingLot(
        List.of(new ParkingSpot("A", 2, SpotSize.MEDIUM), new ParkingSpot("B", 0, SpotSize.MEDIUM)),
        LOWEST_FLOOR, grace);
    Ticket t = lot.enter(new Vehicle("X", VehicleType.CAR), 0);
    System.out.println(t.spotId());

    System.out.println(gate(lot, new Vehicle("Y", VehicleType.CAR), 1));
    System.out.println(gate(lot, new Vehicle("Z", VehicleType.CAR), 2));

    // Ex 4: 8 threads x 5 vehicles into 20 spots -> 20 accepted, 20 rejected.
    ParkingLot big = new ParkingLot(
        java.util.stream.IntStream.range(0, 20)
            .mapToObj(i -> new ParkingSpot("S" + i, 0, SpotSize.LARGE)).toList(),
        new FirstFitStrategy(), grace);
    ExecutorService pool = Executors.newFixedThreadPool(8);
    java.util.concurrent.atomic.AtomicInteger ok = new java.util.concurrent.atomic.AtomicInteger();
    for (int i = 0; i < 40; i++) {
      String plate = "V" + i;
      pool.submit(() -> {
        if (gate(big, new Vehicle(plate, VehicleType.TRUCK), 0).startsWith("ticket")) {
          ok.incrementAndGet();
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.SECONDS);
    System.out.println("accepted=" + ok.get() + " free=" + big.freeSpots());

    // Ex 5: boundary of the hourly policy.
    HourlyFeePolicy hp = new HourlyFeePolicy(10, 20, 50);
    System.out.println(hp.fee(VehicleType.CAR, 60) + " " + hp.fee(VehicleType.CAR, 61));
  }
}
