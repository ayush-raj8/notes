package lld.parking.exam;

import lld.parking.*;

// ANCHOR: predict-fits
public class PredictFits {
  public static void main(String[] args) {
    ParkingSpot large = new ParkingSpot("L", 0, SpotSize.LARGE);
    ParkingSpot medium = new ParkingSpot("M", 0, SpotSize.MEDIUM);
    Vehicle bike = new Vehicle("b", VehicleType.MOTORCYCLE);
    Vehicle truck = new Vehicle("t", VehicleType.TRUCK);
    System.out.println(large.canFit(bike));
    System.out.println(medium.canFit(truck));
    System.out.println(SpotSize.SMALL.compareTo(SpotSize.LARGE));
  }
}
// ANCHOR_END: predict-fits
