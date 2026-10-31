package lld.ratelimit.exam;

import lld.ratelimit.*;

// ANCHOR: predict-keys
public class PredictKeys {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(0);
    RateLimiter r = new FixedWindowLimiter(1, 1000, clock);
    System.out.println(r.tryAcquire("a") + " " + r.tryAcquire("b") + " " + r.tryAcquire("a"));
    clock.advance(1000);
    System.out.println(r.tryAcquire("a") + " " + r.tryAcquire("b"));
  }
}
// ANCHOR_END: predict-keys
