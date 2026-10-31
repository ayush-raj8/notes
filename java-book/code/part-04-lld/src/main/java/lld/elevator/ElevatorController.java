package lld.elevator;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: controller
/** Facade: owns the cabins, routes hall calls, advances simulated time. */
public final class ElevatorController {
  private final List<Elevator> elevators = new ArrayList<>();
  private final DispatchStrategy strategy;

  public ElevatorController(int minFloor, int maxFloor, int count, DispatchStrategy strategy) {
    for (int i = 0; i < count; i++) {
      elevators.add(new Elevator(i, minFloor, maxFloor));
    }
    this.strategy = strategy;
  }

  public synchronized int requestHall(HallCall call) {
    Elevator e = strategy.pick(List.copyOf(elevators), call);
    e.addStop(call.floor());
    return e.id();
  }

  public synchronized void requestCabin(int elevatorId, int floor) {
    elevators.get(elevatorId).addStop(floor);
  }

  public synchronized List<Served> tick() {
    List<Served> events = new ArrayList<>();
    for (Elevator e : elevators) {
      e.tick().ifPresent(f -> events.add(new Served(e.id(), f)));
    }
    return events;
  }

  public synchronized List<Elevator> elevators() {
    return List.copyOf(elevators);
  }
}
// ANCHOR_END: controller
