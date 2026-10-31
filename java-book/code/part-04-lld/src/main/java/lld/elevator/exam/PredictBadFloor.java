package lld.elevator.exam;

import lld.elevator.*;

// ANCHOR: predict-bad-floor
public class PredictBadFloor {
  public static void main(String[] args) {
    Elevator e = new Elevator(0, 0, 9);
    try {
      e.addStop(10);
    } catch (IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
    }
    try {
      new HallCall(3, Direction.IDLE);
    } catch (IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
    }
    System.out.println(e.pendingStops());
  }
}
// ANCHOR_END: predict-bad-floor
