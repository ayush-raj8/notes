package lld.splitwise.exam;

import java.util.LinkedHashMap;
import java.util.Map;
import lld.splitwise.DebtSimplifier;

// ANCHOR: predict-simplify
public class PredictSimplify {
  public static void main(String[] args) {
    Map<String, Long> net = new LinkedHashMap<>();
    net.put("a", 50L);
    net.put("b", -20L);
    net.put("c", -30L);
    DebtSimplifier.simplify(net).forEach(System.out::println);
    try {
      DebtSimplifier.simplify(Map.of("a", 5L));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-simplify
