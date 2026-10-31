import lld.rideshare.RideState;

public class RecordPatternArityBroken {
  static long fare(RideState s) {
    return switch (s) {
      case RideState.Completed(var driver) -> 0;
      default -> -1;
    };
  }
}
