import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lld.splitwise.*;

/** Exercises 1-5 of chapter 58. */
public class SolutionSplitwise {
  // Ex 1: weighted split (e.g. nights stayed) built on ExactSplit with the largest-remainder rule.
  static ExactSplit weighted(long total, Map<String, Integer> weights) {
    int sum = weights.values().stream().mapToInt(Integer::intValue).sum();
    Map<String, Long> out = new LinkedHashMap<>();
    List<Map.Entry<String, Long>> remainders = new ArrayList<>();
    long assigned = 0;
    for (var e : weights.entrySet()) {
      long exact = total * e.getValue();
      out.put(e.getKey(), exact / sum);
      assigned += exact / sum;
      remainders.add(Map.entry(e.getKey(), exact % sum));
    }
    remainders.sort(Comparator.comparingLong((Map.Entry<String, Long> e) -> e.getValue()).reversed());
    for (int i = 0; i < total - assigned; i++) {
      out.merge(remainders.get(i).getKey(), 1L, Long::sum);
    }
    return new ExactSplit(out);
  }

  // Ex 2: who owes the given user, largest first.
  static List<String> owedTo(Splitwise app, String creditor, List<String> users) {
    return users.stream()
        .filter(u -> app.owed(u, creditor) > 0)
        .sorted(Comparator.comparingLong((String u) -> app.owed(u, creditor)).reversed())
        .map(u -> u + "=" + app.owed(u, creditor))
        .toList();
  }

  public static void main(String[] args) {
    Map<String, Integer> nights = new LinkedHashMap<>();
    nights.put("A", 3);
    nights.put("B", 2);
    nights.put("C", 2);
    System.out.println(weighted(100, nights).amounts());

    Splitwise app = SplitwiseDemo.trip();
    System.out.println(owedTo(app, "A", List.of("B", "C", "D")));

    // Ex 3: settle part of a debt, then show the new net balances.
    app.settle("C", "A", 50);
    System.out.println(app.owed("C", "A") + " " + app.netBalances());

    // Ex 4: after everyone follows the simplified transfers the net balances are zero.
    Map<String, Long> net = new LinkedHashMap<>(app.netBalances());
    for (Transfer t : app.simplify()) {
      net.merge(t.from(), t.amount(), Long::sum);
      net.merge(t.to(), -t.amount(), Long::sum);
    }
    System.out.println(net);

    // Ex 5: the money rule: cents as long, never double.
    long cents = Math.round(12.5 * 100);
    System.out.println(cents + " " + (0.1 + 0.2 == 0.3));
  }
}
