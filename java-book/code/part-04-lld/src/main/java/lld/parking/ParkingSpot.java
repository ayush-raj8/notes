package lld.parking;

// ANCHOR: parking-spot
/** A physical spot. Occupancy is mutated only by {@link ParkingLot} while it holds its lock. */
public final class ParkingSpot {
  private final String id;
  private final int floor;
  private final SpotSize size;
  private Vehicle occupant;

  public ParkingSpot(String id, int floor, SpotSize size) {
    this.id = id;
    this.floor = floor;
    this.size = size;
  }

  public String id() { return id; }
  public int floor() { return floor; }
  public SpotSize size() { return size; }
  public boolean isFree() { return occupant == null; }

  public boolean canFit(Vehicle v) {
    return isFree() && size.compareTo(v.type().minimumSpot()) >= 0;
  }

  void park(Vehicle v) {
    if (!canFit(v)) {
      throw new IllegalStateException("spot " + id + " cannot take " + v.plate());
    }
    occupant = v;
  }

  void vacate() {
    occupant = null;
  }
}
// ANCHOR_END: parking-spot
