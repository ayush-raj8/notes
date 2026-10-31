package lld.splitwise;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ANCHOR: splitwise
/**
 * Pairwise ledger. {@code owes[debtor][creditor]} is the net amount debtor owes creditor and is
 * kept one-directional: A→B and B→A never both exist.
 */
public final class Splitwise {
  private final Map<String, User> users = new LinkedHashMap<>();
  private final Map<String, Map<String, Long>> owes = new HashMap<>();
  private long nextExpense = 1;

  public synchronized void addUser(User user) {
    if (users.putIfAbsent(user.id(), user) != null) {
      throw new IllegalArgumentException("duplicate user " + user.id());
    }
  }

  public synchronized Expense addExpense(String paidBy, long total, Split split) {
    if (total <= 0) {
      throw new IllegalArgumentException("total must be positive");
    }
    requireUser(paidBy);
    Map<String, Long> shares = split.shares(total);
    shares.keySet().forEach(this::requireUser);
    for (var e : shares.entrySet()) {
      if (!e.getKey().equals(paidBy) && e.getValue() > 0) {
        addDebt(e.getKey(), paidBy, e.getValue());
      }
    }
    return new Expense("E" + nextExpense++, paidBy, total, split);
  }

  /** Debtor hands money to creditor outside the app. */
  public synchronized void settle(String from, String to, long amount) {
    long owed = owed(from, to);
    if (amount <= 0 || amount > owed) {
      throw new IllegalArgumentException("can settle between 1 and " + owed);
    }
    addDebt(to, from, amount);
  }

  public synchronized long owed(String debtor, String creditor) {
    return owes.getOrDefault(debtor, Map.of()).getOrDefault(creditor, 0L);
  }

  /** Positive: the user is owed money overall; negative: the user owes. */
  public synchronized Map<String, Long> netBalances() {
    Map<String, Long> net = new LinkedHashMap<>();
    users.keySet().forEach(u -> net.put(u, 0L));
    owes.forEach((debtor, row) -> row.forEach((creditor, amount) -> {
      net.merge(debtor, -amount, Long::sum);
      net.merge(creditor, amount, Long::sum);
    }));
    return net;
  }

  public synchronized List<Transfer> simplify() {
    return DebtSimplifier.simplify(netBalances());
  }

  private void requireUser(String id) {
    if (!users.containsKey(id)) {
      throw new IllegalArgumentException("unknown user " + id);
    }
  }

  private void addDebt(String debtor, String creditor, long amount) {
    long reverse = owed(creditor, debtor);
    if (reverse > 0) {
      long left = reverse - amount;
      if (left > 0) {
        owes.get(creditor).put(debtor, left);
        return;
      }
      owes.get(creditor).remove(debtor);
      amount = -left;
    }
    if (amount > 0) {
      owes.computeIfAbsent(debtor, d -> new HashMap<>()).merge(creditor, amount, Long::sum);
    }
  }
}
// ANCHOR_END: splitwise
