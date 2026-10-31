package lld.splitwise;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// ANCHOR: debt-simplifier
/**
 * Greedy settle-up: repeatedly match the largest creditor with the largest debtor. At most n-1
 * transfers. Minimum-transfer settlement is NP-hard; greedy is the usual interview answer.
 */
public final class DebtSimplifier {
  private DebtSimplifier() {}

  private record Entry(String user, long amount) {}

  public static List<Transfer> simplify(Map<String, Long> net) {
    long total = net.values().stream().mapToLong(Long::longValue).sum();
    if (total != 0) {
      throw new IllegalArgumentException("balances must sum to zero, got " + total);
    }
    Comparator<Entry> largestFirst =
        Comparator.comparingLong(Entry::amount).reversed().thenComparing(Entry::user);
    PriorityQueue<Entry> creditors = new PriorityQueue<>(largestFirst);
    PriorityQueue<Entry> debtors = new PriorityQueue<>(largestFirst);
    net.forEach((user, amount) -> {
      if (amount > 0) {
        creditors.add(new Entry(user, amount));
      } else if (amount < 0) {
        debtors.add(new Entry(user, -amount));
      }
    });
    List<Transfer> transfers = new ArrayList<>();
    while (!creditors.isEmpty() && !debtors.isEmpty()) {
      Entry c = creditors.poll();
      Entry d = debtors.poll();
      long pay = Math.min(c.amount(), d.amount());
      transfers.add(new Transfer(d.user(), c.user(), pay));
      if (c.amount() > pay) {
        creditors.add(new Entry(c.user(), c.amount() - pay));
      }
      if (d.amount() > pay) {
        debtors.add(new Entry(d.user(), d.amount() - pay));
      }
    }
    return transfers;
  }
}
// ANCHOR_END: debt-simplifier
