package lld.rideshare;

@FunctionalInterface
public interface FareCalculator {
  long fareCents(RideRequest request);

  /** Static factory on the interface: call it as FareCalculator.standard(), never via an instance. */
  static FareCalculator standard() {
    return r -> r.type().fareCents(r.distanceKm());
  }
}
