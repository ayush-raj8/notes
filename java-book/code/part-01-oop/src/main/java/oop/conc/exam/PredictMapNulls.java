package oop.conc.exam;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ANCHOR: predict-map-nulls
public final class PredictMapNulls {
  public static void main(String[] args) {
    Map<String, String> h = new HashMap<>();
    h.put("k", null);
    System.out.println(h.get("k") + " " + h.containsKey("k"));
    Map<String, String> c = new ConcurrentHashMap<>();
    try {
      c.put("k", null);
      System.out.println("stored");
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
    System.out.println(c.containsKey("k"));
  }
}
// ANCHOR_END: predict-map-nulls
