package oop.coll.exam;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// ANCHOR: predict-to-map
public final class PredictToMap {
  public static void main(String[] args) {
    List<String> words = List.of("kiwi", "fig", "plum", "pear");
    Map<Integer, String> byLen = words.stream()
        .collect(Collectors.toMap(String::length, w -> w, (a, b) -> a + "+" + b, TreeMap::new));
    System.out.println(byLen);
    try {
      words.stream().collect(Collectors.toMap(String::length, w -> w));
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-to-map
