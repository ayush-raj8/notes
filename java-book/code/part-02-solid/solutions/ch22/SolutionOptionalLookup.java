package solid.ocp.solutions;

import java.util.Map;
import java.util.Optional;
import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;

/** Exercise 5: report an unknown type as an empty Optional instead of throwing. */
public final class SolutionOptionalLookup {
  static Optional<Long> discount(Map<String, Discount> rules, String type, long cents) {
    return Optional.ofNullable(rules.get(type)).map(r -> r.amount(cents));
  }

  public static void main(String[] args) {
    Map<String, Discount> rules = Map.of("VIP", new PercentDiscount(10));
    System.out.println(discount(rules, "VIP", 1_000));
    System.out.println(discount(rules, "GUEST", 1_000));
  }
}
