package lld.parking;

// ANCHOR: vehicle-type
public enum VehicleType {
  MOTORCYCLE(SpotSize.SMALL),
  CAR(SpotSize.MEDIUM),
  TRUCK(SpotSize.LARGE);

  private final SpotSize minimumSpot;

  VehicleType(SpotSize minimumSpot) {
    this.minimumSpot = minimumSpot;
  }

  public SpotSize minimumSpot() {
    return minimumSpot;
  }
}
// ANCHOR_END: vehicle-type
