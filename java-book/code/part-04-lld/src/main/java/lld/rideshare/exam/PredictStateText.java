package lld.rideshare.exam;

import lld.rideshare.*;

public class PredictStateText {
  public static void main(String[] args) {
    Driver d = new Driver("d7", "D", VehicleType.SUV, 4.5, new Location(0, 0));
    RideState[] states = {
      new RideState.Requested(),
      new RideState.Matched(d),
      new RideState.InTrip(d),
      new RideState.Completed(d, 450),
      new RideState.Cancelled("no show")
    };
    for (RideState s : states) {
      System.out.println(RideState.describe(s));
    }
    System.out.println(new RideState.Requested().equals(new RideState.Requested()));
    System.out.println(new RideState.Cancelled("x"));
  }
}
