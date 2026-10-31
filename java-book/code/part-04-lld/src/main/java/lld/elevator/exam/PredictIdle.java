package lld.elevator.exam;

import lld.elevator.Elevator;

// ANCHOR: predict-idle
public class PredictIdle {
  public static void main(String[] args) {
    Elevator e = new Elevator(1, 0, 9);
    System.out.println(e.tick().isPresent() + " " + e.direction());
    e.addStop(0);
    System.out.println(e.tick() + " " + e.direction());
  }
}
// ANCHOR_END: predict-idle
