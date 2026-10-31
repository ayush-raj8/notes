package lld.library;

public record PerDayFine(long centsPerDay) implements FinePolicy {
  @Override
  public long fineCents(long daysLate) {
    return Math.max(0, daysLate) * centsPerDay;
  }
}
