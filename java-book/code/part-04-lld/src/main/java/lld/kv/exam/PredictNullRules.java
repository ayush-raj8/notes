package lld.kv.exam;

import lld.kv.MapKvStore;

// ANCHOR: predict-null-rules
public class PredictNullRules {
  public static void main(String[] args) {
    MapKvStore<String, String> kv = new MapKvStore<>();
    try {
      kv.put("k", null);
    } catch (NullPointerException e) {
      System.out.println("value: " + e.getMessage());
    }
    try {
      kv.get(null);
    } catch (NullPointerException e) {
      System.out.println("key: " + e.getMessage());
    }
    System.out.println(kv.get("missing").isPresent());
    System.out.println(kv.size());
  }
}
// ANCHOR_END: predict-null-rules
