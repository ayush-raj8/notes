package oop.coll.exam;

import java.util.LinkedHashMap;
import java.util.Map;

// ANCHOR: predict-map-calls
public final class PredictMapCalls {
  public static void main(String[] args) {
    Map<String, Integer> m = new LinkedHashMap<>();
    System.out.println(m.put("a", 1));
    System.out.println(m.put("a", 2));
    System.out.println(m.putIfAbsent("a", 3));
    System.out.println(m.putIfAbsent("b", 4));
    System.out.println(m.merge("a", 10, Integer::sum));
    System.out.println(m.merge("c", 10, Integer::sum));
    System.out.println(m.get("zzz"));
    System.out.println(m.remove("b") + " " + m.remove("b"));
    System.out.println(m);
  }
}
// ANCHOR_END: predict-map-calls
