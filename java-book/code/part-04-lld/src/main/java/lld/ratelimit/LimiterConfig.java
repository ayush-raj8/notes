package lld.ratelimit;

// ANCHOR: limiter-config
/** "{@code limit} requests per {@code windowMillis}". */
public record LimiterConfig(int limit, long windowMillis) {
  public LimiterConfig {
    if (limit <= 0 || windowMillis <= 0) {
      throw new IllegalArgumentException("limit and window must be positive");
    }
  }
}
// ANCHOR_END: limiter-config
