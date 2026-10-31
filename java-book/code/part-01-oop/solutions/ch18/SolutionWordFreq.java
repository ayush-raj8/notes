package oop.coll.solutions;

import java.util.Map;
import java.util.TreeMap;

/** Exercise 1: count words, print in alphabetical order. */
public final class SolutionWordFreq {
  static Map<String, Integer> count(String text) {
    Map<String, Integer> freq = new TreeMap<>();
    for (String w : text.toLowerCase().split("\\s+")) {
      if (!w.isEmpty()) {
        freq.merge(w, 1, Integer::sum);
      }
    }
    return freq;
  }

  public static void main(String[] args) {
    System.out.println(count("The cat and the hat and THE bat"));
  }
}
