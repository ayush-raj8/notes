package lld.parking.exam;

import lld.parking.*;

// ANCHOR: predict-fee
public class PredictFee {
  public static void main(String[] args) {
    FeePolicy p = new HourlyFeePolicy(10, 20, 50);
    System.out.println(p.fee(VehicleType.CAR, 0));
    System.out.println(p.fee(VehicleType.CAR, 60));
    System.out.println(p.fee(VehicleType.CAR, 61));
    System.out.println(p.fee(VehicleType.TRUCK, 125));
  }
}
// ANCHOR_END: predict-fee
