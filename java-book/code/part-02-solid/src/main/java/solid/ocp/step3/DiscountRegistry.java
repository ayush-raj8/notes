package solid.ocp.step3;

// ANCHOR: step3
import java.util.HashMap;
import java.util.Map;
import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;

/** Step 3: rules are registered from outside. New types need no edit here. */
public final class DiscountRegistry {
  private final Map<String, Discount> rules = new HashMap<>();

  public static DiscountRegistry withDefaults() {
    DiscountRegistry r = new DiscountRegistry();
    r.register("REGULAR", new PercentDiscount(0));
    r.register("VIP", new PercentDiscount(10));
    r.register("STUDENT", new PercentDiscount(5));
    return r;
  }

  public void register(String type, Discount rule) {
    rules.put(type, rule);
  }

  public int size() {
    return rules.size();
  }

  public long discount(String type, long cents) {
    Discount rule = rules.get(type);
    if (rule == null) {
      throw new IllegalArgumentException("unknown customer type: " + type);
    }
    return rule.amount(cents);
  }
}
// ANCHOR_END: step3
