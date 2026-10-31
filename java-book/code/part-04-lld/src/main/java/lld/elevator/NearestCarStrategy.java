package lld.elevator;

import java.util.Comparator;
import java.util.List;

// ANCHOR: nearest-car
/** Cost = distance + queued stops + a penalty when the cabin is travelling away from the caller. */
public final class NearestCarStrategy implements DispatchStrategy {
  static final int AWAY_PENALTY = 10;

  @Override
  public Elevator pick(List<Elevator> elevators, HallCall call) {
    return elevators.stream()
        .min(Comparator.comparingInt((Elevator e) -> cost(e, call)).thenComparingInt(Elevator::id))
        .orElseThrow(() -> new IllegalStateException("no elevators"));
  }

  static int cost(Elevator e, HallCall call) {
    int distance = Math.abs(e.floor() - call.floor());
    boolean away = (e.direction() == Direction.UP && call.floor() < e.floor())
        || (e.direction() == Direction.DOWN && call.floor() > e.floor());
    return distance + e.pendingStops() + (away ? AWAY_PENALTY : 0);
  }
}
// ANCHOR_END: nearest-car
