package lld.rideshare;

import java.util.List;
import java.util.Optional;

/** Strategy: pick one of the already-eligible drivers (available, right vehicle type). */
@FunctionalInterface
public interface MatchingStrategy {
  Optional<Driver> match(RideRequest request, List<Driver> candidates);
}
