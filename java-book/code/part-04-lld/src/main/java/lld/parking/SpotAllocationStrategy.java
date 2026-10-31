package lld.parking;

import java.util.List;
import java.util.Optional;

// ANCHOR: allocation-strategy
/** Strategy: choose among the spots that can fit. The list is already filtered to fitting spots. */
public interface SpotAllocationStrategy {
  Optional<ParkingSpot> choose(List<ParkingSpot> fitting);
}
// ANCHOR_END: allocation-strategy
