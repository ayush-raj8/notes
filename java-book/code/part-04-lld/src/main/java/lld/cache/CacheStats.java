package lld.cache;

// ANCHOR: cache-stats
public record CacheStats(long hits, long misses) {
  public double hitRate() {
    long total = hits + misses;
    return total == 0 ? 0.0 : (double) hits / total;
  }
}
// ANCHOR_END: cache-stats
