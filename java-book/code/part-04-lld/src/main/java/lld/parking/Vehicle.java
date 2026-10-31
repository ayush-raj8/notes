package lld.parking;

// ANCHOR: vehicle
public record Vehicle(String plate, VehicleType type) {
  public Vehicle {
    if (plate == null || plate.isBlank()) {
      throw new IllegalArgumentException("plate required");
    }
  }
}
// ANCHOR_END: vehicle
