package lld.elevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

// ANCHOR: elevator-test
class ElevatorTest {
  private static List<Integer> runToIdle(Elevator e) {
    List<Integer> order = new ArrayList<>();
    for (int guard = 0; guard < 100 && e.pendingStops() > 0; guard++) {
      e.tick().ifPresent(order::add);
    }
    return order;
  }

  @Test
  void scanServesUpwardStopsInFloorOrder() {
    Elevator e = new Elevator(0, 0, 9);
    e.addStop(5);
    e.addStop(2);
    e.addStop(3);
    assertEquals(List.of(2, 3, 5), runToIdle(e));
    assertEquals(Direction.IDLE, e.direction());
  }

  @Test
  void reversesOnlyWhenNothingAhead() {
    Elevator e = new Elevator(0, 0, 9);
    e.addStop(5);
    runToIdle(e);
    e.addStop(7);
    e.addStop(2);
    assertEquals(List.of(7, 2), runToIdle(e));
  }

  @Test
  void stopAtCurrentFloorOpensDoorsOnNextTick() {
    Elevator e = new Elevator(0, 0, 9);
    e.addStop(0);
    assertEquals(0, e.tick().orElseThrow());
  }

  @Test
  void rejectsFloorOutsideRange() {
    Elevator e = new Elevator(0, 0, 9);
    assertThrows(IllegalArgumentException.class, () -> e.addStop(-1));
    assertThrows(IllegalArgumentException.class, () -> e.addStop(10));
    assertThrows(IllegalArgumentException.class, () -> new HallCall(3, Direction.IDLE));
  }

  @Test
  void nearestIdleCarAnswersHallCall() {
    ElevatorController c = new ElevatorController(0, 9, 2, new NearestCarStrategy());
    c.requestCabin(0, 8);
    for (int i = 0; i < 4; i++) {
      c.tick();
    }
    assertEquals(1, c.requestHall(new HallCall(2, Direction.UP)));
  }

  @Test
  void carTravellingAwayIsPenalised() {
    ElevatorController c = new ElevatorController(0, 9, 2, new NearestCarStrategy());
    c.requestCabin(0, 9);
    for (int i = 0; i < 5; i++) {
      c.tick();
    }
    Elevator e0 = c.elevators().get(0);
    assertEquals(Direction.UP, e0.direction());
    HallCall below = new HallCall(4, Direction.UP);
    assertTrue(NearestCarStrategy.cost(e0, below) >= NearestCarStrategy.AWAY_PENALTY);
    assertEquals(1, c.requestHall(below));
  }

  @Test
  void everyRequestIsEventuallyServed() {
    ElevatorController c = new ElevatorController(0, 9, 3, new NearestCarStrategy());
    int[] floors = {7, 3, 9, 1, 5};
    for (int f : floors) {
      c.requestHall(new HallCall(f, f > 4 ? Direction.DOWN : Direction.UP));
    }
    List<Integer> served = new ArrayList<>();
    for (int t = 0; t < 60; t++) {
      c.tick().forEach(s -> served.add(s.floor()));
    }
    assertEquals(List.of(1, 3, 5, 7, 9), served.stream().sorted().toList());
  }
}
// ANCHOR_END: elevator-test
