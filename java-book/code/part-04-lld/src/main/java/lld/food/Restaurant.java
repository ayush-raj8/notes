package lld.food;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Restaurant {
  private final String id;
  private final String name;
  private final Location location;
  private final Map<String, MenuItem> menu = new LinkedHashMap<>();

  public Restaurant(String id, String name, Location location, List<MenuItem> items) {
    this.id = id;
    this.name = name;
    this.location = location;
    items.forEach(i -> menu.put(i.id(), i));
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public Location location() {
    return location;
  }

  public MenuItem item(String itemId) {
    MenuItem item = menu.get(itemId);
    if (item == null) {
      throw new IllegalArgumentException("unknown item: " + itemId);
    }
    return item;
  }
}
