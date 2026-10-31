package lld.parking;

// ANCHOR: lot-full
/** Checked: callers at the gate must decide what to show the driver. */
public class LotFullException extends Exception {
  public LotFullException(Vehicle v) {
    super("no spot for " + v.type() + " " + v.plate());
  }
}
// ANCHOR_END: lot-full
