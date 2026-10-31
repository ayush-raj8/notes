import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lld.elevator.*;

/** Exercises 1-5 of chapter 52. */
public class SolutionElevator {
  // Ex 1: fewest queued stops wins (lambda: DispatchStrategy has one abstract method).
  static final DispatchStrategy LEAST_BUSY =
      (cars, call) -> cars.stream().min(Comparator.comparingInt(Elevator::pendingStops)).orElseThrow();

  // Ex 2: advance time until every cabin is idle; return the number of ticks used.
  static int runUntilIdle(ElevatorController c, List<Served> log) {
    int ticks = 0;
    while (c.elevators().stream().anyMatch(e -> e.pendingStops() > 0) && ticks < 1000) {
      log.addAll(c.tick());
      ticks++;
    }
    return ticks;
  }

  public static void main(String[] args) {
    ElevatorController two = new ElevatorController(0, 9, 2, LEAST_BUSY);
    two.requestCabin(0, 4);
    System.out.println("least busy answers with E" + two.requestHall(new HallCall(6, Direction.DOWN)));

    // Ex 3: one cabin, hall call at 9, cabin request 2.
    ElevatorController one = new ElevatorController(0, 9, 1, new NearestCarStrategy());
    one.requestHall(new HallCall(9, Direction.DOWN));
    one.requestCabin(0, 2);
    List<Served> log = new ArrayList<>();
    int ticks = runUntilIdle(one, log);
    System.out.println(log + " in " + ticks + " ticks");

    // Ex 4: invalid floor is rejected.
    try {
      one.requestCabin(0, 42);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }

    // Ex 5: stops {4, 1} from floor 0: nearest first, then continue up.
    ElevatorController scan = new ElevatorController(0, 9, 1, new NearestCarStrategy());
    scan.requestCabin(0, 4);
    scan.requestCabin(0, 1);
    List<Served> order = new ArrayList<>();
    System.out.println(runUntilIdle(scan, order) + " ticks " + order);
  }
}
