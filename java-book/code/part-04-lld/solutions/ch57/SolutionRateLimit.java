import java.util.List;
import lld.ratelimit.*;

/** Exercises 1-5 of chapter 57. */
public class SolutionRateLimit {
  // Ex 1: a request must pass every limiter. && short-circuits, so the second limiter is not
  // charged when the first rejects. The reverse still happens: the first IS charged when the
  // second rejects. Fixing that needs check-then-commit on both (see the extension table).
  static RateLimiter allOf(RateLimiter strict, RateLimiter loose) {
    return key -> strict.tryAcquire(key) && loose.tryAcquire(key);
  }

  // Ex 2: a deny-list in front of any limiter, as a lambda.
  static RateLimiter withDenyList(List<String> denied, RateLimiter next) {
    return key -> !denied.contains(key) && next.tryAcquire(key);
  }

  static int count(RateLimiter r, ManualClock c, String key, long from, long to, long step) {
    int n = 0;
    for (long t = from; t < to; t += step) {
      c.set(t);
      if (r.tryAcquire(key)) {
        n++;
      }
    }
    return n;
  }

  public static void main(String[] args) {
    ManualClock c1 = new ManualClock(0);
    RateLimiter both = allOf(
        new FixedWindowLimiter(2, 1000, c1),
        new FixedWindowLimiter(5, 10_000, c1));
    System.out.println("allOf: " + count(both, c1, "u", 0, 10_000, 100));

    ManualClock c2 = new ManualClock(0);
    RateLimiter deny = withDenyList(List.of("bot"), new FixedWindowLimiter(1, 1000, c2));
    System.out.println(deny.tryAcquire("bot") + " " + deny.tryAcquire("human"));

    // Ex 3: 20 requests/second for 3 seconds against limit 5 per second.
    for (Algorithm a : Algorithm.values()) {
      ManualClock c = new ManualClock(0);
      RateLimiter r = a.create(new LimiterConfig(5, 1000), c);
      System.out.println(a + " allowed " + count(r, c, "u", 0, 3000, 50) + " of 60");
    }

    // Ex 4: boundary burst: 900..1100 every 20 ms, limit 3 per 1000.
    for (Algorithm a : Algorithm.values()) {
      ManualClock c = new ManualClock(0);
      RateLimiter r = a.create(new LimiterConfig(3, 1000), c);
      System.out.println(a + " in [900,1100): " + count(r, c, "u", 900, 1100, 20));
    }

    // Ex 5: a bucket that refills 2 tokens/second, capacity 5, one request every 250 ms for 10 s.
    ManualClock c5 = new ManualClock(0);
    RateLimiter slow = new TokenBucketLimiter(5, 2, 1000, c5);
    System.out.println("steady: " + count(slow, c5, "u", 0, 10_000, 250) + " of 40");
  }
}
