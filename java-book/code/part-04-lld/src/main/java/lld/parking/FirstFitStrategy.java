package lld.parking;

import java.util.List;
import java.util.Optional;

// ANCHOR: first-fit
/** Lowest list position wins (list is ordered floor by floor, nearest the entrance first). */
public final class FirstFitStrategy implements SpotAllocationStrategy {
  @Override
  public Optional<ParkingSpot> choose(List<ParkingSpot> fitting) {
    return fitting.stream().findFirst();
  }
}
// ANCHOR_END: first-fit
