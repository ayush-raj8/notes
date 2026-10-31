import lld.parking.*;

public class ParkingPackagePrivateBroken {
  public static void main(String[] args) {
    ParkingSpot spot = new ParkingSpot("S1", 0, SpotSize.MEDIUM);
    spot.park(new Vehicle("A", VehicleType.CAR));
  }
}
