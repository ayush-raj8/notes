package lld.parking;

import java.util.EnumMap;
import java.util.Map;

// ANCHOR: hourly-fee
/** Per-type hourly rate; any started hour is billed; minimum one hour. */
public final class HourlyFeePolicy implements FeePolicy {
  private final Map<VehicleType, Long> ratePerHour = new EnumMap<>(VehicleType.class);

  public HourlyFeePolicy(long motorcycle, long car, long truck) {
    ratePerHour.put(VehicleType.MOTORCYCLE, motorcycle);
    ratePerHour.put(VehicleType.CAR, car);
    ratePerHour.put(VehicleType.TRUCK, truck);
  }

  @Override
  public long fee(VehicleType type, long minutes) {
    long hours = Math.max(1, (minutes + 59) / 60);
    return hours * ratePerHour.get(type);
  }
}
// ANCHOR_END: hourly-fee
