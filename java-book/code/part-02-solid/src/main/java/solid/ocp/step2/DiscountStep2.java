package solid.ocp.step2;

// ANCHOR: step2
import java.util.HashMap;
import java.util.Map;
import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;

/** Step 2: lookup replaces the chain, and unknown types fail loudly. Rules are still hard-wired. */
public final class DiscountStep2 {
  private final Map<String, Discount> rules = new HashMap<>();

  public DiscountStep2() {
    rules.put("REGULAR", new PercentDiscount(0));
    rules.put("VIP", new PercentDiscount(10));
    rules.put("STUDENT", new PercentDiscount(5));
  }

  public long discount(String type, long cents) {
    Discount rule = rules.get(type);
    if (rule == null) {
      throw new IllegalArgumentException("unknown customer type: " + type);
    }
    return rule.amount(cents);
  }
}
// ANCHOR_END: step2
