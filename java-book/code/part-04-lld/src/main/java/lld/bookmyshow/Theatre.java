package lld.bookmyshow;

import java.util.List;

public record Theatre(String id, String name, String city, List<Screen> screens) {
  public Theatre {
    screens = List.copyOf(screens);
  }
}
