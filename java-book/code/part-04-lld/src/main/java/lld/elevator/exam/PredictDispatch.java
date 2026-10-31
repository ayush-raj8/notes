package lld.elevator.exam;

import lld.elevator.*;

// ANCHOR: predict-dispatch
public class PredictDispatch {
  public static void main(String[] args) {
    ElevatorController c = new ElevatorController(0, 9, 2, new NearestCarStrategy());
    c.requestCabin(0, 8);
    for (int i = 0; i < 4; i++) {
      c.tick();
    }
    System.out.println(c.elevators());
    System.out.println(c.requestHall(new HallCall(2, Direction.UP)));
    System.out.println(c.requestHall(new HallCall(6, Direction.DOWN)));
  }
}
// ANCHOR_END: predict-dispatch
