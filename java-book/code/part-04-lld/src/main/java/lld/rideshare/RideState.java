package lld.rideshare;

/** Ride life cycle as a closed set of records: each state carries exactly the data it owns. */
public sealed interface RideState
    permits RideState.Requested,
        RideState.Matched,
        RideState.InTrip,
        RideState.Completed,
        RideState.Cancelled {
  record Requested() implements RideState {}

  record Matched(Driver driver) implements RideState {}

  record InTrip(Driver driver) implements RideState {}

  record Completed(Driver driver, long fareCents) implements RideState {}

  record Cancelled(String reason) implements RideState {}

  static String describe(RideState state) {
    return switch (state) {
      case Requested r -> "waiting for a driver";
      case Matched(Driver d) -> "driver " + d.id() + " on the way";
      case InTrip(Driver d) -> "riding with " + d.id();
      case Completed(Driver d, long fare) -> "done, fare " + fare;
      case Cancelled(String reason) -> "cancelled: " + reason;
    };
  }
}
