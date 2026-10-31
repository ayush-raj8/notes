package lld.kv.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lld.kv.MapKvStore;

// ANCHOR: predict-aliasing
public class PredictAliasing {
  public static void main(String[] args) {
    MapKvStore<String, List<Integer>> kv = new MapKvStore<>();
    List<Integer> list = new ArrayList<>(List.of(1, 2));
    kv.put("nums", list);
    list.add(3);
    System.out.println(kv.get("nums").get());
    Set<String> snapshot = kv.keys();
    kv.put("more", List.of());
    System.out.println(snapshot.size() + " " + kv.keys().size());
    try {
      snapshot.add("x");
    } catch (UnsupportedOperationException e) {
      System.out.println("read-only snapshot");
    }
    System.out.println(kv.get("nums").get() == list);
  }
}
// ANCHOR_END: predict-aliasing
