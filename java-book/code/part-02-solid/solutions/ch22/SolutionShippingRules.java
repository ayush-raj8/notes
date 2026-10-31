package solid.ocp.solutions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongUnaryOperator;

/** Exercise 3: apply the same pattern to shipping fees chosen by region. */
public final class SolutionShippingRules {
  public static void main(String[] args) {
    Map<String, LongUnaryOperator> fee = new LinkedHashMap<>();
    fee.put("LOCAL", grams -> 50);
    fee.put("EU", grams -> 200 + grams / 10);
    fee.put("WORLD", grams -> 500 + grams / 5);
    fee.forEach((region, rule) -> System.out.println(region + " " + rule.applyAsLong(1_000)));
  }
}
