package oop.coll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// ANCHOR: coll-map
public final class CollMapDemo {
  public static void main(String[] args) {
    Map<String, Integer> m = new LinkedHashMap<>();
    System.out.println("put new -> " + m.put("a", 1));
    System.out.println("put again -> " + m.put("a", 2)); // returns the OLD value
    m.put("b", 5);
    System.out.println("get missing -> " + m.get("z") + ", getOrDefault -> " + m.getOrDefault("z", 0));
    System.out.println("putIfAbsent(a) -> " + m.putIfAbsent("a", 99) + ", map=" + m);

    Map<String, Integer> counts = new TreeMap<>();
    for (String w : "to be or not to be".split(" ")) {
      counts.merge(w, 1, Integer::sum); // word count in one call
    }
    System.out.println("counts   : " + counts);

    Map<Integer, List<String>> byLen = new TreeMap<>();
    for (String w : List.of("hi", "yo", "hey")) {
      byLen.computeIfAbsent(w.length(), k -> new ArrayList<>()).add(w);
    }
    System.out.println("multimap : " + byLen);

    for (Map.Entry<String, Integer> e : counts.entrySet()) {
      System.out.print(e.getKey() + "=" + e.getValue() + ";");
    }
    System.out.println();

    Map<String, Integer> hm = new HashMap<>();
    hm.put(null, 1); // HashMap allows one null key
    System.out.println("HashMap null key -> " + hm.get(null));
    try {
      new TreeMap<String, Integer>().put(null, 1);
    } catch (NullPointerException e) {
      System.out.println("TreeMap null key -> NullPointerException");
    }
    try {
      Map.of("k", 1, "k", 2);
    } catch (IllegalArgumentException e) {
      System.out.println("Map.of duplicate key -> IllegalArgumentException");
    }

    Map<String, Integer> live = new LinkedHashMap<>(Map.of("x", 1));
    live.keySet().remove("x"); // keySet is a view: removal writes through
    System.out.println("after keySet().remove: " + live);
  }
}
// ANCHOR_END: coll-map
