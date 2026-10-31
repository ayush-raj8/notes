package solid.ocp.rules;

// ANCHOR: discount
@FunctionalInterface
public interface Discount {
  /** Discount amount in cents for a purchase of {@code cents}. */
  long amount(long cents);
}
// ANCHOR_END: discount
