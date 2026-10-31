package oop.coll.solutions;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Exercise 3: group words by length, longest words first inside each group sorted alphabetically. */
public final class SolutionGroupByLength {
  static Map<Integer, List<String>> group(List<String> words) {
    return words.stream()
        .sorted()
        .collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.toList()));
  }

  public static void main(String[] args) {
    System.out.println(group(List.of("pear", "fig", "plum", "kiwi", "apple", "yam")));
  }
}
