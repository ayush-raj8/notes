package lld.parking.exam;

import lld.parking.*;

// ANCHOR: predict-best-fit
public class PredictBestFit {
  public static void main(String[] args) throws LotFullException {
    Vehicle bike = new Vehicle("B1", VehicleType.MOTORCYCLE);
    ParkingLot first = lld.parking.ParkingDemo.smallLot(new FirstFitStrategy());
    ParkingLot best = lld.parking.ParkingDemo.smallLot(new BestFitStrategy());
    System.out.println(first.enter(bike, 0).spotId());
    System.out.println(best.enter(bike, 0).spotId());
  }
}
// ANCHOR_END: predict-best-fit
