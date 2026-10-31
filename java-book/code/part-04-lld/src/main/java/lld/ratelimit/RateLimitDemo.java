package lld.ratelimit;

// ANCHOR: rate-limit-demo
public final class RateLimitDemo {
  private RateLimitDemo() {}

  static String pattern(RateLimiter limiter, ManualClock clock, long[] times, String key) {
    StringBuilder sb = new StringBuilder();
    for (long t : times) {
      clock.set(t);
      sb.append(limiter.tryAcquire(key) ? 'T' : 'F');
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    long[] times = {900, 910, 920, 1000, 1010, 1020, 1900};
    LimiterConfig cfg = new LimiterConfig(3, 1000);
    for (Algorithm a : Algorithm.values()) {
      ManualClock clock = new ManualClock(0);
      System.out.println(a + " " + pattern(a.create(cfg, clock), clock, times, "u1"));
    }
    ManualClock clock = new ManualClock(0);
    RateLimiter perUser = Algorithm.SLIDING_LOG.create(new LimiterConfig(1, 1000), clock);
    System.out.println("alice " + perUser.tryAcquire("alice") + " " + perUser.tryAcquire("alice")
        + ", bob " + perUser.tryAcquire("bob"));
  }
}
// ANCHOR_END: rate-limit-demo
