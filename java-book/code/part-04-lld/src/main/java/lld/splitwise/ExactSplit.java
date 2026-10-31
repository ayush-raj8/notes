package lld.splitwise;

import java.util.LinkedHashMap;
import java.util.Map;

// ANCHOR: exact-split
/** Caller states each amount; they must add up to the total. */
public record ExactSplit(Map<String, Long> amounts) implements Split {
  public ExactSplit {
    amounts = new LinkedHashMap<>(amounts);
  }

  @Override
  public Map<String, Long> shares(long total) {
    long sum = amounts.values().stream().mapToLong(Long::longValue).sum();
    if (sum != total) {
      throw new IllegalArgumentException("exact amounts sum to " + sum + ", expected " + total);
    }
    return new LinkedHashMap<>(amounts);
  }
}
// ANCHOR_END: exact-split
