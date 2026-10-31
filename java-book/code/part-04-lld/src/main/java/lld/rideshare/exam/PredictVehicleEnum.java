package lld.rideshare.exam;

import lld.rideshare.VehicleType;

public class PredictVehicleEnum {
  public static void main(String[] args) {
    System.out.println(VehicleType.SEDAN.fareCents(10));
    System.out.println(VehicleType.BIKE.fareCents(0));
    System.out.println(VehicleType.SUV.compareTo(VehicleType.BIKE));
    System.out.println(VehicleType.valueOf("SUV").name() + VehicleType.values().length);
    System.out.println(VehicleType.BIKE.getClass() == VehicleType.class);
  }
}
