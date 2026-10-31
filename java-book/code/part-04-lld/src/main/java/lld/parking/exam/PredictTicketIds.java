package lld.parking.exam;

import lld.parking.*;

// ANCHOR: predict-ticket-ids
public class PredictTicketIds {
  public static void main(String[] args) throws LotFullException {
    ParkingLot lot = ParkingDemo.smallLot(new FirstFitStrategy());
    Ticket a = lot.enter(new Vehicle("A", VehicleType.CAR), 0);
    lot.exit(a.id(), 30);
    Ticket b = lot.enter(new Vehicle("B", VehicleType.CAR), 40);
    System.out.println(a.id() + " " + b.id() + " " + b.spotId());
    try {
      lot.exit(a.id(), 50);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-ticket-ids
