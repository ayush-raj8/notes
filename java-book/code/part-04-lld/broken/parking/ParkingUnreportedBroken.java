import lld.parking.*;

public class ParkingUnreportedBroken {
  public static void main(String[] args) {
    ParkingLot lot = ParkingDemo.smallLot(new FirstFitStrategy());
    lot.enter(new Vehicle("A", VehicleType.CAR), 0);
  }
}
