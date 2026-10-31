package lld.kv.exam;

import lld.kv.MapKvStore;

// ANCHOR: predict-put-return
public class PredictPutReturn {
  public static void main(String[] args) {
    MapKvStore<String, Integer> kv = new MapKvStore<>();
    System.out.println(kv.put("a", 1));
    System.out.println(kv.put("a", 2));
    System.out.println(kv.putIfAbsent("a", 3));
    System.out.println(kv.putIfAbsent("b", 4));
    System.out.println(kv.delete("a") + " " + kv.delete("a"));
    System.out.println(kv.getOrDefault("a", -1) + " " + kv.get("b").map(v -> v * 10).orElse(0));
    System.out.println(kv.compareAndSet("b", 99, 5) + " " + kv.compareAndSet("b", 4, 5) + " " + kv.get("b").get());
  }
}
// ANCHOR_END: predict-put-return
