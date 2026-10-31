package lld.elevator.exam;

import lld.elevator.Elevator;

// ANCHOR: predict-scan-order
public class PredictScanOrder {
  public static void main(String[] args) {
    Elevator e = new Elevator(0, 0, 9);
    e.addStop(5);
    e.addStop(2);
    e.addStop(3);
    int ticks = 0;
    while (e.pendingStops() > 0) {
      ticks++;
      int t = ticks;
      e.tick().ifPresent(f -> System.out.println("tick " + t + " served " + f));
    }
  }
}
// ANCHOR_END: predict-scan-order
