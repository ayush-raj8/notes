package lld.splitwise.exam;

import java.util.LinkedHashMap;
import java.util.Map;
import lld.splitwise.PercentSplit;

// ANCHOR: predict-percent
public class PredictPercent {
  public static void main(String[] args) {
    Map<String, Integer> p = new LinkedHashMap<>();
    p.put("x", 33);
    p.put("y", 33);
    p.put("z", 34);
    System.out.println(new PercentSplit(p).shares(101));
    try {
      new PercentSplit(Map.of("x", 60, "y", 30)).shares(100);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-percent
