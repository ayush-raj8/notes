package lld.splitwise;

import java.util.LinkedHashMap;
import java.util.Map;

// ANCHOR: percent-split
/** Whole percentages that sum to 100. Each share rounds down; leftover cents go to the first entries,
 * so pass a LinkedHashMap when the order matters. */
public record PercentSplit(Map<String, Integer> percents) implements Split {
  public PercentSplit {
    percents = new LinkedHashMap<>(percents);
  }

  @Override
  public Map<String, Long> shares(long total) {
    int sum = percents.values().stream().mapToInt(Integer::intValue).sum();
    if (sum != 100) {
      throw new IllegalArgumentException("percentages sum to " + sum + ", expected 100");
    }
    Map<String, Long> out = new LinkedHashMap<>();
    long assigned = 0;
    for (var e : percents.entrySet()) {
      long share = total * e.getValue() / 100;
      out.put(e.getKey(), share);
      assigned += share;
    }
    long leftover = total - assigned;
    for (var e : out.entrySet()) {
      if (leftover == 0) {
        break;
      }
      e.setValue(e.getValue() + 1);
      leftover--;
    }
    return out;
  }
}
// ANCHOR_END: percent-split
