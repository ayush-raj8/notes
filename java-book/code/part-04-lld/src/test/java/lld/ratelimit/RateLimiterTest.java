package lld.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

// ANCHOR: ratelimit-test
class RateLimiterTest {
  private static int allowed(RateLimiter r, ManualClock c, String key, long... times) {
    int n = 0;
    for (long t : times) {
      c.set(t);
      if (r.tryAcquire(key)) {
        n++;
      }
    }
    return n;
  }

  @Test
  void tokenBucketAllowsBurstThenRefillsAtSteadyRate() {
    ManualClock c = new ManualClock(0);
    RateLimiter r = new TokenBucketLimiter(2, 1, 1000, c);
    assertTrue(r.tryAcquire("k"));
    assertTrue(r.tryAcquire("k"));
    assertFalse(r.tryAcquire("k"));
    c.advance(999);
    assertFalse(r.tryAcquire("k"));
    c.advance(1);
    assertTrue(r.tryAcquire("k"));
    assertFalse(r.tryAcquire("k"));
  }

  @Test
  void tokenBucketNeverExceedsCapacityAfterLongIdle() {
    ManualClock c = new ManualClock(0);
    RateLimiter r = new TokenBucketLimiter(3, 3, 1000, c);
    c.advance(1_000_000);
    assertEquals(3, allowed(r, c, "k", 1_000_000, 1_000_000, 1_000_000, 1_000_000, 1_000_000));
  }

  @Test
  void fixedWindowAllowsDoubleBurstAcrossBoundary() {
    ManualClock c = new ManualClock(0);
    RateLimiter r = new FixedWindowLimiter(3, 1000, c);
    assertEquals(6, allowed(r, c, "k", 900, 910, 920, 1000, 1010, 1020));
  }

  @Test
  void slidingLogHasNoBoundaryBurst() {
    ManualClock c = new ManualClock(0);
    RateLimiter r = new SlidingWindowLogLimiter(3, 1000, c);
    assertEquals(3, allowed(r, c, "k", 900, 910, 920, 1000, 1010, 1020));
    assertEquals(1, allowed(r, c, "k", 1900));
  }

  @Test
  void keysAreIndependent() {
    ManualClock c = new ManualClock(0);
    RateLimiter r = new FixedWindowLimiter(1, 1000, c);
    assertTrue(r.tryAcquire("a"));
    assertTrue(r.tryAcquire("b"));
    assertFalse(r.tryAcquire("a"));
  }

  @Test
  void configAndConstructorsRejectNonPositiveValues() {
    assertThrows(IllegalArgumentException.class, () -> new LimiterConfig(0, 1000));
    assertThrows(IllegalArgumentException.class, () -> new TokenBucketLimiter(0, 1, 1000, () -> 0L));
    assertThrows(IllegalArgumentException.class, () -> new FixedWindowLimiter(1, 0, () -> 0L));
    assertThrows(IllegalArgumentException.class, () -> new SlidingWindowLogLimiter(-1, 10, () -> 0L));
  }

  @Test
  void clockGoingBackwardsDoesNotMintTokens() {
    ManualClock c = new ManualClock(10_000);
    RateLimiter r = new TokenBucketLimiter(1, 1, 1000, c);
    assertTrue(r.tryAcquire("k"));
    c.set(5_000);
    assertFalse(r.tryAcquire("k"));
  }

  @Test
  void everyAlgorithmIsExactUnderContention() throws Exception {
    for (Algorithm a : Algorithm.values()) {
      ManualClock c = new ManualClock(0);
      RateLimiter r = a.create(new LimiterConfig(10, 60_000), c);
      int threads = 64;
      ExecutorService pool = Executors.newFixedThreadPool(threads);
      CountDownLatch go = new CountDownLatch(1);
      AtomicInteger ok = new AtomicInteger();
      for (int i = 0; i < threads; i++) {
        pool.submit(() -> {
          go.await();
          for (int j = 0; j < 5; j++) {
            if (r.tryAcquire("shared")) {
              ok.incrementAndGet();
            }
          }
          return null;
        });
      }
      go.countDown();
      pool.shutdown();
      assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
      assertEquals(10, ok.get(), a.name());
    }
  }
}
// ANCHOR_END: ratelimit-test
