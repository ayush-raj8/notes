package solid.ocp.solutions;

import solid.ocp.rules.PercentDiscount;
import solid.ocp.step3.DiscountRegistry;

/** Exercise 1: add SENIOR (15%) without editing any existing class. */
public final class SolutionSeniorDiscount {
  public static void main(String[] args) {
    DiscountRegistry r = DiscountRegistry.withDefaults();
    r.register("SENIOR", new PercentDiscount(15));
    System.out.println(r.discount("SENIOR", 10_000));
  }
}
