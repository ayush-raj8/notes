package lld.elevator.exam;

import lld.elevator.Elevator;

// ANCHOR: predict-reverse
public class PredictReverse {
  public static void main(String[] args) {
    Elevator e = new Elevator(0, 0, 9);
    e.addStop(5);
    while (e.tick().isEmpty()) { }
    System.out.println(e);
    e.addStop(7);
    e.addStop(2);
    while (e.pendingStops() > 0) {
      e.tick().ifPresent(f -> System.out.println("served " + f));
    }
    System.out.println(e);
  }
}
// ANCHOR_END: predict-reverse
