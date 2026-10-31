package lld.parking.exam;

import lld.parking.*;

// ANCHOR: predict-double-entry
public class PredictDoubleEntry {
  public static void main(String[] args) throws LotFullException {
    ParkingLot lot = ParkingDemo.smallLot(new BestFitStrategy());
    Vehicle car = new Vehicle("KA-9", VehicleType.CAR);
    lot.enter(car, 0);
    try {
      lot.enter(car, 5);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(lot.freeSpots());
  }
}
// ANCHOR_END: predict-double-entry
