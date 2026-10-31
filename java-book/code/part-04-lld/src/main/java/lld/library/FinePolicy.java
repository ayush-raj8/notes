package lld.library;

/** Strategy: how a number of late days becomes money (cents). Functional, so lambdas work. */
@FunctionalInterface
public interface FinePolicy {
  long fineCents(long daysLate);

  static FinePolicy none() {
    return daysLate -> 0;
  }
}
