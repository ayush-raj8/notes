package lld.parking;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// ANCHOR: best-fit
/** Smallest sufficient size wins, so a motorcycle does not take a LARGE spot while SMALL is free. */
public final class BestFitStrategy implements SpotAllocationStrategy {
  @Override
  public Optional<ParkingSpot> choose(List<ParkingSpot> fitting) {
    return fitting.stream().min(Comparator.comparing(ParkingSpot::size));
  }
}
// ANCHOR_END: best-fit
