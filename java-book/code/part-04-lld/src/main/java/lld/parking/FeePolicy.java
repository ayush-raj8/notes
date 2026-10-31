package lld.parking;

// ANCHOR: fee-policy
public interface FeePolicy {
  /** Fee in whole currency units for a stay of {@code minutes}. */
  long fee(VehicleType type, long minutes);
}
// ANCHOR_END: fee-policy
