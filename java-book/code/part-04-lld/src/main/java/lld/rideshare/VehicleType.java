package lld.rideshare;

/** Enum with data: constants carry their tariff. The constructor is implicitly private. */
public enum VehicleType {
  BIKE(20, 8),
  SEDAN(50, 12),
  SUV(80, 18);

  private final long baseCents;
  private final long perKmCents;

  VehicleType(long baseCents, long perKmCents) {
    this.baseCents = baseCents;
    this.perKmCents = perKmCents;
  }

  public long fareCents(int km) {
    return baseCents + perKmCents * km;
  }
}
