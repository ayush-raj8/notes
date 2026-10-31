package lld.food;

public final class DeliveryPartner {
  private final String id;
  private final String name;
  private Location location;
  private boolean available = true;

  public DeliveryPartner(String id, String name, Location location) {
    this.id = id;
    this.name = name;
    this.location = location;
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

  public boolean isAvailable() {
    return available;
  }

  void setAvailable(boolean available) {
    this.available = available;
  }

  void moveTo(Location location) {
    this.location = location;
  }
}
