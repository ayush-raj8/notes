package lld.ratelimit;

import java.util.function.LongSupplier;

// ANCHOR: algorithm
/** Factory keyed by enum; the switch expression is exhaustive, so a new constant forces a new case. */
public enum Algorithm {
  TOKEN_BUCKET, FIXED_WINDOW, SLIDING_LOG;

  public RateLimiter create(LimiterConfig config, LongSupplier clock) {
    return switch (this) {
      case TOKEN_BUCKET -> new TokenBucketLimiter(config.limit(), config.limit(), config.windowMillis(), clock);
      case FIXED_WINDOW -> new FixedWindowLimiter(config.limit(), config.windowMillis(), clock);
      case SLIDING_LOG -> new SlidingWindowLogLimiter(config.limit(), config.windowMillis(), clock);
    };
  }
}
// ANCHOR_END: algorithm
