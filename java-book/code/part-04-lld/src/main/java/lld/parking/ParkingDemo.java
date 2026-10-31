package lld.parking;

import java.util.List;

// ANCHOR: parking-demo
public final class ParkingDemo {
  private ParkingDemo() {}

  public static ParkingLot smallLot(SpotAllocationStrategy strategy) {
    return new ParkingLot(
        List.of(
            new ParkingSpot("F0-L1", 0, SpotSize.LARGE),
            new ParkingSpot("F0-S1", 0, SpotSize.SMALL),
            new ParkingSpot("F1-M1", 1, SpotSize.MEDIUM)),
        strategy,
        new HourlyFeePolicy(10, 20, 50));
  }

  public static void main(String[] args) throws LotFullException {
    ParkingLot lot = smallLot(new BestFitStrategy());
    Ticket bike = lot.enter(new Vehicle("KA-01", VehicleType.MOTORCYCLE), 0);
    Ticket car = lot.enter(new Vehicle("KA-02", VehicleType.CAR), 10);
    System.out.println(bike.spotId() + " " + car.spotId());
    System.out.println("free: " + lot.freeSpots());
    System.out.println("bike fee: " + lot.exit(bike.id(), 59));
    System.out.println("car fee: " + lot.exit(car.id(), 190));
    try {
      lot.enter(new Vehicle("KA-03", VehicleType.TRUCK), 200);
      lot.enter(new Vehicle("KA-04", VehicleType.TRUCK), 201);
    } catch (LotFullException e) {
      System.out.println("rejected: " + e.getMessage());
    }
  }
}
// ANCHOR_END: parking-demo
