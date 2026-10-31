package lld.ratelimit;

// ANCHOR: rate-limiter
/** Decides, per key (user, IP, API key), whether one more request is allowed right now. */
@FunctionalInterface
public interface RateLimiter {
  boolean tryAcquire(String key);
}
// ANCHOR_END: rate-limiter
