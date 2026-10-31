package oop.coll.solutions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/** Exercise 7: remove duplicates but keep first-seen order. */
public final class SolutionDedupeOrder {
  static <T> List<T> dedupe(List<T> in) {
    return new ArrayList<>(new LinkedHashSet<>(in));
  }

  public static void main(String[] args) {
    System.out.println(dedupe(List.of(3, 1, 3, 2, 1, 4)));
  }
}
