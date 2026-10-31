package lld.atm;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

// ANCHOR: cash-dispenser
/** Note cassette. {@code plan} does not change stock; {@code dispense} does. */
public final class CashDispenser {
  private final Map<Denomination, Integer> notes = new EnumMap<>(Denomination.class);

  public CashDispenser(int n500, int n200, int n100) {
    notes.put(Denomination.N500, n500);
    notes.put(Denomination.N200, n200);
    notes.put(Denomination.N100, n100);
  }

  public int total() {
    int sum = 0;
    for (var e : notes.entrySet()) {
      sum += e.getKey().value() * e.getValue();
    }
    return sum;
  }

  public Optional<Map<Denomination, Integer>> plan(int amount) {
    Map<Denomination, Integer> plan = new EnumMap<>(Denomination.class);
    return search(Denomination.values(), 0, amount, plan) ? Optional.of(plan) : Optional.empty();
  }

  private boolean search(Denomination[] order, int i, int remaining, Map<Denomination, Integer> plan) {
    if (remaining == 0) {
      return true;
    }
    if (i == order.length) {
      return false;
    }
    Denomination d = order[i];
    for (int n = Math.min(notes.get(d), remaining / d.value()); n >= 0; n--) {
      if (n > 0) {
        plan.put(d, n);
      }
      if (search(order, i + 1, remaining - n * d.value(), plan)) {
        return true;
      }
      plan.remove(d);
    }
    return false;
  }

  public void dispense(Map<Denomination, Integer> plan) {
    plan.forEach((d, n) -> {
      if (notes.get(d) < n) {
        throw new IllegalStateException("not enough " + d);
      }
    });
    plan.forEach((d, n) -> notes.merge(d, -n, Integer::sum));
  }
}
// ANCHOR_END: cash-dispenser
