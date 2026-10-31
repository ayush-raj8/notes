package oop.generics.solutions;

import java.util.List;

/** Exercise 1: max of a list with a flexible bound. */
public final class SolutionMaxList {
  static <T extends Comparable<? super T>> T max(List<? extends T> xs) {
    T best = xs.get(0);
    for (T x : xs) {
      if (x.compareTo(best) > 0) {
        best = x;
      }
    }
    return best;
  }

  public static void main(String[] args) {
    System.out.println(max(List.of(3, 9, 4)));
    System.out.println(max(List.of("b", "z", "a")));
  }
}
