package lld.rideshare;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class NearestDriver implements MatchingStrategy {
  @Override
  public Optional<Driver> match(RideRequest request, List<Driver> candidates) {
    return candidates.stream()
        .min(
            Comparator.comparingInt((Driver d) -> d.location().distanceTo(request.pickup()))
                .thenComparing(Driver::id));
  }
}
