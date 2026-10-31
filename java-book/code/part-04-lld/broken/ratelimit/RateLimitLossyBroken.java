import lld.ratelimit.ManualClock;

public class RateLimitLossyBroken {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(0);
    int now = clock.getAsLong();
    System.out.println(now);
  }
}
