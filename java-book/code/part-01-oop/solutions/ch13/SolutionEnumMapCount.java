package oop.immut.solutions;

import java.util.EnumMap;
import java.util.Map;

/** Exercise 4: count enum occurrences with an EnumMap. */
public final class SolutionEnumMapCount {
  enum Kind { A, B, C }

  public static void main(String[] args) {
    Kind[] input = {Kind.B, Kind.A, Kind.B, Kind.C, Kind.B};
    Map<Kind, Integer> counts = new EnumMap<>(Kind.class);
    for (Kind k : input) {
      counts.merge(k, 1, Integer::sum);
    }
    System.out.println(counts);
  }
}
