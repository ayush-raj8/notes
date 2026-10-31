import java.util.List;
import lld.rideshare.Driver;
import lld.rideshare.MatchingStrategy;
import lld.rideshare.RideRequest;

public class OptionalUnwrapBroken {
  static Driver pick(MatchingStrategy s, RideRequest r, List<Driver> ds) {
    Driver d = s.match(r, ds);
    return d;
  }
}
