package lld.rideshare;

/** Owns its state; every transition pattern-matches the current state and rejects the rest. */
public final class Ride {
  private final String id;
  private final RideRequest request;
  private RideState state = new RideState.Requested();

  Ride(String id, RideRequest request) {
    this.id = id;
    this.request = request;
  }

  public String id() {
    return id;
  }

  public RideRequest request() {
    return request;
  }

  public RideState state() {
    return state;
  }

  void match(Driver driver) {
    state =
        switch (state) {
          case RideState.Requested r -> new RideState.Matched(driver);
          default -> throw illegal("match");
        };
  }

  void start() {
    state =
        switch (state) {
          case RideState.Matched(Driver d) -> new RideState.InTrip(d);
          default -> throw illegal("start");
        };
  }

  void complete(long fareCents) {
    state =
        switch (state) {
          case RideState.InTrip(Driver d) -> new RideState.Completed(d, fareCents);
          default -> throw illegal("complete");
        };
  }

  void cancel(String reason) {
    state =
        switch (state) {
          case RideState.Requested r -> new RideState.Cancelled(reason);
          case RideState.Matched m -> new RideState.Cancelled(reason);
          default -> throw illegal("cancel");
        };
  }

  private IllegalStateException illegal(String action) {
    return new IllegalStateException("cannot " + action + " from " + state.getClass().getSimpleName());
  }
}
