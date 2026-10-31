import java.util.List;
import lld.rideshare.Driver;

public class BoundedGenericBroken {
  static <T extends Comparable<T>> T best(List<T> items) {
    return items.get(0);
  }

  static Driver top(List<Driver> drivers) {
    return best(drivers);
  }
}
