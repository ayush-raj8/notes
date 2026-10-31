package lld.splitwise;

import java.util.Map;

// ANCHOR: split
/**
 * How one expense is divided. Amounts are long cents. {@code shares} returns each participant's
 * share and the shares always sum to exactly {@code total}, or the call throws.
 */
public sealed interface Split permits EqualSplit, ExactSplit, PercentSplit {
  Map<String, Long> shares(long total);
}
// ANCHOR_END: split
