package lld.ratelimit.exam;

import lld.ratelimit.*;

// ANCHOR: predict-fixed-boundary
public class PredictFixedBoundary {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(0);
    RateLimiter r = new FixedWindowLimiter(2, 1000, clock);
    StringBuilder sb = new StringBuilder();
    for (long t : new long[] {990, 995, 999, 1000, 1001, 1002}) {
      clock.set(t);
      sb.append(r.tryAcquire("k") ? 'T' : 'F');
    }
    System.out.println(sb);
  }
}
// ANCHOR_END: predict-fixed-boundary
