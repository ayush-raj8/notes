package lld.rideshare;

/** Decorator: multiplies another calculator by percent/100 (integer maths, floors). */
public final class SurgeFare implements FareCalculator {
  private final FareCalculator inner;
  private final int percent;

  public SurgeFare(FareCalculator inner, int percent) {
    if (percent < 100) {
      throw new IllegalArgumentException("surge percent must be >= 100: " + percent);
    }
    this.inner = inner;
    this.percent = percent;
  }

  @Override
  public long fareCents(RideRequest request) {
    return inner.fareCents(request) * percent / 100;
  }
}
