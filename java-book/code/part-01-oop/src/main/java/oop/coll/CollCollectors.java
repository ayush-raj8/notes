package oop.coll;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// ANCHOR: coll-collectors
public final class CollCollectors {
  public static void main(String[] args) {
    List<String> words = List.of("apple", "bob", "cat", "avocado", "bee", "cow");

    System.out.println("joining       : " + words.stream().collect(Collectors.joining(", ", "[", "]")));
    System.out.println("groupingBy 1st: " + words.stream()
        .collect(Collectors.groupingBy(w -> w.charAt(0), TreeMap::new, Collectors.toList())));
    System.out.println("counting      : " + words.stream()
        .collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.counting())));
    System.out.println("partitioningBy: " + words.stream()
        .collect(Collectors.partitioningBy(w -> w.length() > 3)));
    System.out.println("mapping       : " + words.stream()
        .collect(Collectors.groupingBy(w -> w.charAt(0), TreeMap::new,
            Collectors.mapping(String::toUpperCase, Collectors.joining("/")))));
    System.out.println("summingInt    : " + words.stream().collect(Collectors.summingInt(String::length)));
    System.out.println("averagingInt  : " + words.stream().collect(Collectors.averagingInt(String::length)));

    Map<String, Integer> lengths = words.stream()
        .collect(Collectors.toMap(w -> w, String::length, (a, b) -> a, TreeMap::new));
    System.out.println("toMap         : " + lengths);

    try {
      List.of("a", "b", "a").stream().collect(Collectors.toMap(w -> w, w -> 1));
    } catch (IllegalStateException e) {
      System.out.println("toMap duplicate: " + e.getMessage());
    }
  }
}
// ANCHOR_END: coll-collectors
