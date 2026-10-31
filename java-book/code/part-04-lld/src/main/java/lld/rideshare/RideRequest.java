package lld.rideshare;

public record RideRequest(Rider rider, Location pickup, Location drop, VehicleType type) {
  public int distanceKm() {
    return pickup.distanceTo(drop);
  }
}
