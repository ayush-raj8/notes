package lld.rideshare;

/** Status and location change only through RideService (package-private setters). */
public final class Driver {
  private final String id;
  private final String name;
  private final VehicleType type;
  private final double rating;
  private Location location;
  private DriverStatus status = DriverStatus.OFFLINE;

  public Driver(String id, String name, VehicleType type, double rating, Location location) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.rating = rating;
    this.location = location;
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public VehicleType type() {
    return type;
  }

  public double rating() {
    return rating;
  }

  public Location location() {
    return location;
  }

  public DriverStatus status() {
    return status;
  }

  void setStatus(DriverStatus status) {
    this.status = status;
  }

  void moveTo(Location location) {
    this.location = location;
  }
}
