package lld.rideshare;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Best rating among drivers within {@code radiusKm} of the pickup. */
public final class HighestRated implements MatchingStrategy {
  private final int radiusKm;

  public HighestRated(int radiusKm) {
    this.radiusKm = radiusKm;
  }

  @Override
  public Optional<Driver> match(RideRequest request, List<Driver> candidates) {
    return candidates.stream()
        .filter(d -> d.location().distanceTo(request.pickup()) <= radiusKm)
        .max(Comparator.comparingDouble(Driver::rating).thenComparing(Driver::id, Comparator.reverseOrder()));
  }
}
