package solid.ocp.step1;

// ANCHOR: step1
import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;

/** Step 1: each branch builds a rule object. The chain is still closed to extension. */
public final class DiscountStep1 {
  public static long discount(String type, long cents) {
    Discount rule;
    if (type.equals("VIP")) {
      rule = new PercentDiscount(10);
    } else if (type.equals("STUDENT")) {
      rule = new PercentDiscount(5);
    } else {
      rule = new PercentDiscount(0);
    }
    return rule.amount(cents);
  }
}
// ANCHOR_END: step1
