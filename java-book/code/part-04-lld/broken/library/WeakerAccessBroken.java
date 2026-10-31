import lld.library.FinePolicy;

public class WeakerAccessBroken implements FinePolicy {
  long fineCents(long daysLate) {
    return 0;
  }
}
