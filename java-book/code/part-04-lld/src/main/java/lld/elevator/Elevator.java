package lld.elevator;

import java.util.Optional;
import java.util.TreeSet;

// ANCHOR: elevator
/**
 * One cabin. SCAN rule: keep going in the current direction while a stop remains ahead, then
 * reverse. A tick moves one floor, or opens the doors when the cabin is on a stop.
 */
public final class Elevator {
  private final int id;
  private final int minFloor;
  private final int maxFloor;
  private int floor;
  private Direction direction = Direction.IDLE;
  private final TreeSet<Integer> stops = new TreeSet<>();

  public Elevator(int id, int minFloor, int maxFloor) {
    this.id = id;
    this.minFloor = minFloor;
    this.maxFloor = maxFloor;
    this.floor = minFloor;
  }

  public synchronized void addStop(int target) {
    if (target < minFloor || target > maxFloor) {
      throw new IllegalArgumentException(
          "floor " + target + " outside " + minFloor + ".." + maxFloor);
    }
    stops.add(target);
  }

  public synchronized Optional<Integer> tick() {
    if (stops.remove(floor)) {
      settle();
      return Optional.of(floor);
    }
    if (stops.isEmpty()) {
      direction = Direction.IDLE;
      return Optional.empty();
    }
    direction = nextDirection();
    floor += direction == Direction.UP ? 1 : -1;
    if (stops.remove(floor)) {
      settle();
      return Optional.of(floor);
    }
    return Optional.empty();
  }

  private void settle() {
    if (stops.isEmpty()) {
      direction = Direction.IDLE;
    }
  }

  private Direction nextDirection() {
    return switch (direction) {
      case UP -> stops.higher(floor) != null ? Direction.UP : Direction.DOWN;
      case DOWN -> stops.lower(floor) != null ? Direction.DOWN : Direction.UP;
      case IDLE -> {
        Integer up = stops.higher(floor);
        Integer down = stops.lower(floor);
        if (up == null) {
          yield Direction.DOWN;
        }
        if (down == null) {
          yield Direction.UP;
        }
        yield (up - floor) <= (floor - down) ? Direction.UP : Direction.DOWN;
      }
    };
  }

  public int id() { return id; }
  public synchronized int floor() { return floor; }
  public synchronized Direction direction() { return direction; }
  public synchronized int pendingStops() { return stops.size(); }

  @Override
  public synchronized String toString() {
    return "E" + id + "@" + floor + " " + direction;
  }
}
// ANCHOR_END: elevator
