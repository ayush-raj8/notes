package lld.ratelimit.exam;

import lld.ratelimit.*;

// ANCHOR: predict-burst
public class PredictBurst {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(0);
    RateLimiter r = new TokenBucketLimiter(2, 1, 1000, clock);
    System.out.println(r.tryAcquire("k") + " " + r.tryAcquire("k") + " " + r.tryAcquire("k"));
    clock.advance(999);
    System.out.println(r.tryAcquire("k"));
    clock.advance(1);
    System.out.println(r.tryAcquire("k"));
    System.out.println(r.tryAcquire("k"));
  }
}
// ANCHOR_END: predict-burst
