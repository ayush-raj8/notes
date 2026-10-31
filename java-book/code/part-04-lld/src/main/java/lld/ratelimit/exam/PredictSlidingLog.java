package lld.ratelimit.exam;

import lld.ratelimit.*;

// ANCHOR: predict-sliding-log
public class PredictSlidingLog {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(0);
    RateLimiter r = new SlidingWindowLogLimiter(2, 1000, clock);
    StringBuilder sb = new StringBuilder();
    for (long t : new long[] {0, 500, 600, 1000, 1001, 1500}) {
      clock.set(t);
      sb.append(r.tryAcquire("k") ? 'T' : 'F');
    }
    System.out.println(sb);
  }
}
// ANCHOR_END: predict-sliding-log
