package lld.scheduler;

import java.time.Duration;

// ANCHOR: retry-policy
/** Total attempts allowed and the first back-off; each further failure doubles the wait. */
public record RetryPolicy(int maxAttempts, Duration backoff) {
  public RetryPolicy {
    if (maxAttempts < 1) {
      throw new IllegalArgumentException("maxAttempts must be >= 1: " + maxAttempts);
    }
    if (maxAttempts > 1 && (backoff.isNegative() || backoff.isZero())) {
      throw new IllegalArgumentException("backoff must be positive when retrying: " + backoff);
    }
  }

  public static RetryPolicy none() {
    return new RetryPolicy(1, Duration.ZERO);
  }

  public static RetryPolicy of(int maxAttempts, Duration backoff) {
    return new RetryPolicy(maxAttempts, backoff);
  }

  /** Wait before the next attempt after {@code failures} consecutive failures (1-based). */
  public Duration delayAfter(int failures) {
    return backoff.multipliedBy(1L << (failures - 1));
  }

  public boolean allowsRetryAfter(int failures) {
    return failures < maxAttempts;
  }
}
// ANCHOR_END: retry-policy
