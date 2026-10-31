package lld.splitwise;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ANCHOR: equal-split
/** Equal parts; the leftover cents go one each to the first participants, in list order. */
public record EqualSplit(List<String> participants) implements Split {
  public EqualSplit {
    if (participants == null || participants.isEmpty()) {
      throw new IllegalArgumentException("need at least one participant");
    }
    if (participants.stream().distinct().count() != participants.size()) {
      throw new IllegalArgumentException("duplicate participant");
    }
    participants = List.copyOf(participants);
  }

  @Override
  public Map<String, Long> shares(long total) {
    int n = participants.size();
    long base = total / n;
    long extra = total % n;
    Map<String, Long> out = new LinkedHashMap<>();
    for (int i = 0; i < n; i++) {
      out.put(participants.get(i), base + (i < extra ? 1 : 0));
    }
    return out;
  }
}
// ANCHOR_END: equal-split
